(ns udp-spike.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TRANSMITTER:

(defn init-xmitter-state [max-packet-size content-bytes]
  {:max-packet-size max-packet-size
   :content-bytes   content-bytes
   :identifier      0})

(defn add-identifier
  ([content-state] (byte-array (conj (vec content-state) 127)))
  ([content-state limit identifier]
   (byte-array (conj (vec (for [i (range (dec limit))] (get content-state i)))
                     identifier))))

(defn packet-to-receiver [xmitter-state]
  (let [content-state (xmitter-state :content-bytes)
        limit (xmitter-state :max-packet-size)
        identifier (xmitter-state :identifier)]
    (if-not (nil? content-state)
      (if (< (alength content-state) limit)
        (add-identifier content-state)
        (add-identifier content-state limit identifier))
      nil)))

(defn content-update [content-state limit]
  (byte-array
    (for [i (range (alength content-state))
          :when (>= i limit)]
      (get content-state i))))

(defn xmitter-handle [xmitter-state packet-from-receiver]
  (let [content-state (xmitter-state :content-bytes)
        limit (xmitter-state :max-packet-size)
        identifier (xmitter-state :identifier)]
    (if-not (nil? packet-from-receiver)
      (if (and (= packet-from-receiver identifier)
               (not= packet-from-receiver 127))
        {:max-packet-size limit
         :content-bytes   (content-update content-state limit)
         :identifier      (inc identifier)}
        nil)
      xmitter-state)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; RECEIVER:

(defn init-receiver-state [max-packet-size]
  {:max-packet-size     max-packet-size
   :last-block-received 0})

(defn receiver-handle [receiver-state packet-from-xmitter]
  ;(assoc receiver-state ...)
  {:content-bytes packet-from-xmitter})

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
