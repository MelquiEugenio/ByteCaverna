(ns udp-spike.core
  (:import (java.util Arrays)
           (java.io File FileInputStream FileOutputStream)
           (java.net DatagramPacket DatagramSocket InetAddress)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TRANSMITTER:

(defn init-xmitter-state [max-packet-size content-bytes]
  {:max-packet-size  max-packet-size
   :content-bytes    content-bytes
   :block-identifier -128})

(defn in-put-file [path]
  (let [is    (-> ^String path File. FileInputStream.)
        bytes (byte-array (-> ^String path File. .length))]
    (.read is bytes)
    (.close is)
    bytes))

(defn add-identifier
  ([content]
   (byte-array (conj (vec content) 127)))
  ([content max-size identifier]
   (byte-array (conj (subvec (vec content) 0 (dec max-size)) identifier))))

(defn packet-to-receiver [xmitter-state]
  (if-let [content    (:content-bytes xmitter-state)]
    (let  [max-size   (:max-packet-size xmitter-state)
           identifier (:block-identifier xmitter-state)]
      (if (< (alength content) max-size)
        (do
          (println "packet-to-receiver" (vec (add-identifier content)))
          (add-identifier content))
        (do
          (println "packet-to-receiver" (vec (add-identifier content max-size identifier)))
          (add-identifier content max-size identifier))))))

(defn xmitter-handle [xmitter-state packet-from-receiver]
  (println "packet-from-receiver:" (vec packet-from-receiver))
  (let [block-expected (first packet-from-receiver)]
    (cond
      (= block-expected 127)
        (dissoc xmitter-state :content-bytes)
      (= block-expected (inc (:block-identifier xmitter-state)))
        (let [ret (update-in xmitter-state [:block-identifier] inc)
              max-size (:max-packet-size xmitter-state)]
          (update-in ret [:content-bytes] #(byte-array (subvec (vec %) (dec max-size)))))
      :else xmitter-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; RECEIVER:

(defn init-receiver-state [max-packet-size]
  {:max-packet-size   max-packet-size
   :content-bytes     (byte-array 0)
   :block-expected   -128})

(defn out-put-file [bytes]
  (let [os (-> "/home/melqui/test(c?pia).txt" File. FileOutputStream.)]
    (.write os ^bytes bytes)
    (.close os)))

(defn packet-to-xmitter [receiver-state]
  (println "packet-to-xmitter:" (vec (byte-array [(:block-expected receiver-state)])) )
  (byte-array [(:block-expected receiver-state)]))

(defn receiver-handle [packet-from-xmitter receiver-state]
  (println "packet-from-xmitter:" (vec packet-from-xmitter))
  (let [identifier (last packet-from-xmitter)]
    (cond
      (= (:block-expected receiver-state) 127)
        receiver-state
      (= identifier 127)                                    ;last pack
        (let [block (drop-last packet-from-xmitter)
              ret (update-in receiver-state [:content-bytes] #(byte-array (concat % block)))]
          (out-put-file (:content-bytes ret))
          (assoc ret :block-expected 127))
      (= identifier (:block-expected receiver-state))
        (let [ret   (update-in receiver-state [:block-expected] inc)
              block (drop-last packet-from-xmitter)]
          (update-in ret [:content-bytes] #(byte-array (concat % block))))
      :else receiver-state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TESTE

;envia e recebe pacotes dentro de limites.
;lida com pacotes perdidos.
;lida com pacotes desordenados.
;le um arquivo e o reescreve.
;proximo: transmitir via UDP.

(defn connection-simulator []
  (atom []))

(defn send-packet [conn packet-to-receiver]
  (if (= (rand-int 2) 0)                              ;50%
    packet-to-receiver
    (when (= (rand-int 2) 0)                          ;25%
      (swap! conn conj packet-to-receiver)            ;add pack to conn-simu
      (get @conn (rand-int (dec (.length @conn))))))) ;return a lost pack

(defn testa-transmissao-bytes [max-packet-size content-bytes]
  (let [conn1 (connection-simulator)
        conn2 (connection-simulator)
        result
        (loop [receiver-state (init-receiver-state max-packet-size)
               xmitter-state  (init-xmitter-state max-packet-size content-bytes)]
          (if-let [packet-to-receiver (packet-to-receiver xmitter-state)]
            (do
              (assert (<= (alength packet-to-receiver) max-packet-size))
              (let [packet-to-receiver (send-packet conn1 packet-to-receiver)
                    receiver-state     (if packet-to-receiver
                                         (receiver-handle packet-to-receiver receiver-state)
                                         receiver-state)
                    packet-to-xmitter  (send-packet conn2 (packet-to-xmitter receiver-state))
                    xmitter-state      (if packet-to-xmitter
                                         (xmitter-handle xmitter-state packet-to-xmitter)
                                         xmitter-state)]
                (println (:block-expected receiver-state) (:block-identifier xmitter-state))
                (recur receiver-state xmitter-state)))
            (:content-bytes receiver-state)))]
    (println (vec content-bytes))
    (println (vec result))
    (Arrays/equals ^bytes result ^bytes content-bytes)))

(defn testa-transmissao [path]
  (testa-transmissao-bytes 10 (in-put-file path)))

(defn testa-protocolo []
  (testa-transmissao "/home/melqui/Develop/GitHub/ByteCaverna/udp-spike/test/test.txt")
  )


