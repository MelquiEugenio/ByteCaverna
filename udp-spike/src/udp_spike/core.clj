(ns udp-spike.core
  (:import (java.util Arrays)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TRANSMITTER:

(defn init-xmitter-state [max-packet-size content-bytes]
  {:max-packet-size max-packet-size
   :content-bytes   content-bytes
   :last-block-sent 0})

(defn add-identifier
  ([content] (byte-array (conj (vec content) 127)))
  ([content max-size identifier]
   (byte-array (conj (subvec (vec content) 0 (dec max-size)) identifier))))

(defn packet-to-receiver [xmitter-state]
  (if-let [content (:content-bytes xmitter-state)]
    (let [max-size        (:max-packet-size xmitter-state)
          last-block-sent (:last-block-sent xmitter-state)]
      (if (< (alength content) max-size)
        (add-identifier content)
        (add-identifier content max-size last-block-sent)
        ))))

(defn xmitter-handle [xmitter-state packet-from-receiver]
  (let [last-block-sent (:last-block-sent xmitter-state)
        max-size (:max-packet-size xmitter-state)]
    (cond
      (= (first packet-from-receiver) last-block-sent)
      (let [ret (update-in xmitter-state [:last-block-sent] inc)]
        (update-in ret [:content-bytes] #(byte-array (subvec (vec %) (dec max-size)))))
      (= (first packet-from-receiver) 127)
      (dissoc xmitter-state :content-bytes)
      :else xmitter-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; RECEIVER:

(defn init-receiver-state [max-packet-size]
  {:max-packet-size     max-packet-size
   :last-block-received 0
   :content-bytes       (byte-array 0)})

(defn contents-received [receiver-state]
  (:content-bytes receiver-state))

(defn packet-to-xmitter [receiver-state packet-from-xmitter]
  (cond
    (= (last packet-from-xmitter) (:last-block-received receiver-state))
    (byte-array (vector (:last-block-received receiver-state)))
    (= (last packet-from-xmitter) 127)
    (byte-array (vector 127))
    :else nil))

(defn receiver-handle [receiver-state packet-from-xmitter]
  (cond
    (= (last packet-from-xmitter) (:last-block-received receiver-state))
    (let [ret (update-in receiver-state [:last-block-received] inc)
          block (drop-last packet-from-xmitter)]
      (update-in ret [:content-bytes] #(byte-array (concat % block))))
    (= (last packet-from-xmitter) 127)
    (let [ret (assoc receiver-state :last-block-received 127)
          block (drop-last packet-from-xmitter)]
      (update-in ret [:content-bytes] #(byte-array (concat % block))))
    :else receiver-state))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TESTE

(defn lost-packet-simu [packet-to-receiver]
  (if (even? (rand-int 2))
    packet-to-receiver))

(defn testa-transmissao-bytes [max-packet-size content-bytes]
  (let [result
        (loop [receiver-state (init-receiver-state max-packet-size)
               xmitter-state (init-xmitter-state max-packet-size content-bytes)]
          (if-let [packet-to-receiver (packet-to-receiver xmitter-state)]
            (do
              (assert (<= (alength packet-to-receiver) max-packet-size))
              (recur
                (receiver-handle receiver-state (lost-packet-simu packet-to-receiver))
                (xmitter-handle xmitter-state (packet-to-xmitter receiver-state packet-to-receiver))))
            (contents-received receiver-state)))]
    (Arrays/equals result content-bytes)))

(defn testa-transmissao [string]
  (println string)
  (testa-transmissao-bytes 10 (.getBytes string)))

(defn testa-protocolo []
  (testa-transmissao "")
  (testa-transmissao "A")
  (testa-transmissao "ABC")
  (testa-transmissao "1234567")
  (testa-transmissao "12345678")
  (testa-transmissao "123456789")
  (testa-transmissao "1234567890")
  (testa-transmissao "12345678901")
  (testa-transmissao "1234567890rctvbhnjmioklpokhuitfdrdcvbnumioplokjihuygtf")
  )
