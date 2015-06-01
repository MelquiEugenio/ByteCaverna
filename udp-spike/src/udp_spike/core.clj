(ns udp-spike.core)

(defn create-xmitter [max-block-size content-bytes]
  {:max-block-size      max-block-size
   :content-bytes       content-bytes
   :next-packet-to-send 0})

(defn create-receiver [max-block-size]
  {:max-block-size max-block-size
   :last-block-received 0})

(defn packet-to-receiver [xmitter]
  (let [packet (xmitter :content-bytes)
        limit (xmitter :max-block-size)]
    (if (< (alength packet) limit)
      (byte-array (conj (vec packet) 127))
      ())))

(defn receiver-handle [receiver packet]
  {:content-bytes packet})

(defn packet-to-xmitter [receiver]
  nil)

(defn xmitter-handle [xmitter packet]
  (update-in xmitter [:next-packet-to-send] inc))

(defn contents-received [receiver]
  (receiver :content-bytes))

(defn testa-transmissao-bytes [max-block-size content-bytes]
  (let [result
        (loop [receiver (create-receiver max-block-size)
               xmitter (create-xmitter max-block-size content-bytes)]
          (if-let [packet (packet-to-receiver xmitter)]
            (do
              (assert (<= (alength packet) max-block-size))
              (recur
                (receiver-handle receiver packet)
                (xmitter-handle xmitter (packet-to-xmitter receiver))))
            (contents-received receiver)))]
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
  (testa-transmissao "1234567891"))
