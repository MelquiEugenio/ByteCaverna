(ns udp-spike.core)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TRANSMITTER:

(defn init-xmitter-state [max-packet-size content-bytes]
  {:max-packet-size max-packet-size
   :content-bytes   content-bytes
   :block-identifier      0})

#_(defn add-identifier
  ([content] (byte-array (conj (vec content) 127)))
  ([content max-size identifier]
   (byte-array (conj (subvec (vec content) 0 (dec max-size)) identifier))))

(defn packet-to-receiver [xmitter-state]
  (if-let [content (xmitter-state :content-bytes)
        ;block-identifier (xmitter-state :block-identifier)
        ]
      (if (<= (alength content) (xmitter-state :max-packet-size))
        content
        (byte-array (subvec (vec content) 0 (xmitter-state :max-packet-size)))
        ;(add-identifier content)
        ;(add-identifier content max-size block-identifier)
        )))

(defn content-update [content max-size]
  (byte-array (subvec (vec content) max-size)))

(defn xmitter-handle [xmitter-state packet-from-receiver]
  (let [content (xmitter-state :content-bytes)
        max-size (xmitter-state :max-packet-size)
        identifier (xmitter-state :block-identifier)]
    (if (<= (alength content) max-size)
      nil
      {:max-packet-size max-size
       :content-bytes    (content-update content max-size)
       :block-identifier (inc identifier)})

    #_(if-not (nil? packet-from-receiver)
      (if (and (= (first packet-from-receiver) identifier)
               (not= packet-from-receiver 127))             ;retirar identifier
        {:max-packet-size  max-size
         :content-bytes    (content-update content max-size)
         :block-identifier (inc identifier)}
        nil)
      xmitter-state)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; RECEIVER:

(defn init-receiver-state [max-packet-size]
  {:max-packet-size     max-packet-size
   :last-block-received 0
   :content-bytes (byte-array 0)})

(defn receiver-handle [receiver-state packet-from-xmitter]
  (let [ret (update-in receiver-state [:last-block-received] inc)]
    (update-in ret [:content-bytes] #(byte-array (concat % packet-from-xmitter)))))

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
