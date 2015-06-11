(ns dojos.mars-rover
  (:require
    [midje.sweet :refer :all]
    [clojure.core.async :refer [alt!! timeout >! <! >!! <!! chan go dropping-buffer]]))

; (do (require 'midje.repl) (midje.repl/autotest))

;====================================== UTILS

(defn >!!?
  ([ch v]
    (>!!? ch v 200))
  ([ch v timeout-millis]
    (alt!!
      (timeout timeout-millis) false
      [[ch v]] true)))

(defn <!!?

  ([ch]
    (<!!? ch 200))

  ([ch timeout-millis]
    (alt!!
      (timeout timeout-millis) :timeout
      ch ([v] v))))


;==================================== SINCRONO

(def moves {:up    {:fx identity :fy inc}
            :right {:fx inc      :fy identity}
            :left  {:fx dec      :fy identity}
            :down  {:fx identity :fy dec}})

(defn create-rover-old []
  (atom {:x 0
         :y 0
         :orientation :right}))

(defn position [rover]
  (select-keys @rover [:x :y]))

(defn move [rover direction]
  (let [move (moves direction)]
    (swap! rover update-in [:x] (:fx move))
    (swap! rover update-in [:y] (:fy move))))

(def reverse-direction {:up :down, :down :up, :left :right, :right :left})
(defn move-forward  [rover] (move rover ( @rover :orientation)) nil)
(defn move-backward [rover] (move rover ((@rover :orientation) reverse-direction)))

(def next-left {:right :up, :up :left, :left :down, :down :right})
(defn turn-left [rover]
  (swap! rover update-in [:orientation] next-left))

;=================================================== ASSINCRONO


(def command->fn {:position     position
                  :move-forward move-forward
                  :move-backward move-backward})

(defn create-rover [commands replies]
  (let [rover (create-rover-old)]
    (go
      (loop []
        (let [command (<! commands)]
          ((command->fn command) rover)
          ;(Thread/sleep 50)
          (>! replies (position rover)))
        (recur)))))


;=================================================== TESTES:

(fact "The rover can???"
  (let [commands (chan 2)
        replies (chan (dropping-buffer 1))]
    (create-rover commands replies)
    (>!!? commands :position)
    (>!!? commands :move-forward)
    (>!!? commands :move-backward)

    (<!!? replies) => {:x 0 :y 0}
    (<!!? replies) => {:x 1 :y 0}
    (<!!? replies) => {:x 0 :y 0}


    ;    (turn-left rover)
    ;    (move-forward rover)
    ;    (position rover) => {:x 0 :y 1}
    ;    (move-backward rover)
    ;    (position rover) => {:x 0 :y 0}
    ;    (turn-left rover)
    ;    (move-forward rover)
    ;    (position rover) => {:x -1 :y 0}
    ;    (move-backward rover)
    ;    (position rover) => {:x 0 :y 0}
    ;    (turn-left rover)
    ;    (move-forward rover)
    ;    (position rover) => {:x 0 :y -1}
    ;    (move-backward rover)
    ;    (position rover) => {:x 0 :y 0}
    ))



;=================================================== TESTES SINCRONOS:

(fact "The rover can move forward"
  (let [rover (create-rover-old)]
    (position rover) => {:x 0 :y 0}
    (move-forward rover)
    (position rover) => {:x 1 :y 0}
    (move-backward rover)
    (position rover) => {:x 0 :y 0}
    (turn-left rover)
    (move-forward rover)
    (position rover) => {:x 0 :y 1}
    (move-backward rover)
    (position rover) => {:x 0 :y 0}
    (turn-left rover)
    (move-forward rover)
    (position rover) => {:x -1 :y 0}
    (move-backward rover)
    (position rover) => {:x 0 :y 0}
    (turn-left rover)
    (move-forward rover)
    (position rover) => {:x 0 :y -1}
    (move-backward rover)
    (position rover) => {:x 0 :y 0}))
