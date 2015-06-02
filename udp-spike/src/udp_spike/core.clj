(ns udp-spike.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TRANSMITTER:

(defn init-xmitter-state [max-packet-size content-bytes]
  {:max-packet-size   max-packet-size
   :content-bytes     content-bytes
   :next-block-to-send 0})

(defn packet-to-receiver [xmitter-state]
  (let [content-state (xmitter-state :content-bytes)
        limit (xmitter-state :max-packet-size)
        identifier (xmitter-state :next-block-to-send)]
    (if (< (alength content-state) limit)
      (byte-array (conj (vec content-state) 127))
      (byte-array (conj (vec (for [i (range (dec limit))]
                               (get content-state i))) identifier)))))

(defn xmitter-handle [xmitter-state packet-from-receiver]
  ;se packet-from-receiver confirmar a entrega
  (let [content-state (xmitter-state :content-bytes)
        limit (xmitter-state :max-packet-size)]
    (do
      (assoc xmitter-state :next-block-to-send inc)
      (assoc xmitter-state :content-bytes (byte-array
                                            (for [i (range (alength content-state))
                                                  :when (>= i limit)]
                                              (get content-state i)))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; RECEIVER:

(defn init-receiver-state [max-packet-size]
  {:max-packet-size     max-packet-size
   :last-block-received 0})

(defn receiver-handle [receiver-state packet-from-xmitter]
  ;(assoc receiver-state ...)
  {:content-bytes packet})

(defn packet-to-xmitter [receiver-state]
  nil)

(defn contents-received [receiver-state]
  (receiver-state :content-bytes))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TESTE

(defn testa-transmissao-bytes [max-packet-size content-bytes]
  (let [result
        (loop [receiver-state (init-receiver-state max-packet-size)
               xmitter-state (init-xmitter-state max-packet-size content-bytes)]
          (if-let [packet-to-receiver (packet-to-receiver xmitter-state)]
            (do
              (assert (<= (alength packet-to-receiver) max-packet-size))
              (recur
                (receiver-handle receiver-state packet-to-receiver)
                (xmitter-handle xmitter-state (packet-to-xmitter receiver-state))))
            (contents-received receiver-state)))]
    (assert (= result content-bytes))))

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
  (testa-transmissao "1234567890rctvbhnjmioklpokhuitfdrdcvbnumioplokjihuygtf"))
