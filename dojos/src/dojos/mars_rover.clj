(ns dojos.mars-rover
  (:require
    [midje.sweet :refer :all]))

; (do (require 'midje.repl) (midje.repl/autotest))

(def moves {:up    {:fx identity :fy inc}
            :right {:fx inc      :fy identity}
            :left  {:fx dec      :fy identity}
            :down  {:fx identity :fy dec}})

(defn create-rover []
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
(defn move-forward  [rover] (move rover ( @rover :orientation)))
(defn move-backward [rover] (move rover ((@rover :orientation) reverse-direction)))

(def next-left {:right :up, :up :left, :left :down, :down :right})
(defn turn-left [rover]
  (swap! rover update-in [:orientation] next-left))

;=================================================== TESTES:

(fact "The rover can move forward"
  (let [rover (create-rover)]
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
