(ns udp-spike.core
  (:import (java.util Arrays)
           (java.io File FileInputStream FileOutputStream)
           (java.net DatagramPacket DatagramSocket InetAddress)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; SENDER:

(defn init-sender-state [max-packet-size content-bytes]
  {:max-packet-size  max-packet-size
   :content-bytes    content-bytes
   :block-to-send    -128})

(defn append-id [block id]
  (byte-array (conj block id)))

(defn packet-to-receiver [state]
  (if-let [content  (:content-bytes state)]
    (let  [max-size (:max-packet-size state)
           id       (:block-to-send state)]
      (if (< (alength content) max-size)
        (append-id (vec content) 127)
        (let [block (subvec (vec content) 0 (dec max-size))]
          (append-id block id))))))

(defn xmitter-handle [packet state]
  (let [expecting (first packet)
        to-send  (:block-to-send state)]
    (cond
      (= expecting 127)                       ;means last pack was received
        (dissoc state :content-bytes)
      (= expecting (inc to-send))
        (let [ret       (update-in state [:block-to-send] inc)
              max-size  (:max-packet-size state)]
          (update-in ret [:content-bytes] #(byte-array (subvec (vec %) (dec max-size)))))
      :else state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; RECEIVER:

(defn init-receiver-state [max-packet-size]
  {:max-packet-size   max-packet-size
   :content-bytes     (byte-array 0)
   :block-expected    -128})

(defn packet-to-xmitter [state]
  (byte-array [(:block-expected state)]))

(defn write-file [bytes]
  (let [os (-> "/home/melqui/test(c?pia).png" File. FileOutputStream.)]
    (.write os ^bytes bytes)
    (.close os)))

(defn receiver-handle [packet state]
  (let [id        (last packet)
        block     (drop-last packet)
        expected  (:block-expected state)]
    (cond
      (= expected 127)                       ;means the last pack already came
        state
      (= id 127)                             ;last pack arrivin'
        (let [ret (update-in state [:content-bytes] #(byte-array (concat % block)))]
          (write-file (:content-bytes ret))
          (assoc ret :block-expected 127))
      (= expected id)                        ;expected pack arrivin'
        (let [ret (update-in state [:block-expected] inc)]
          (update-in ret [:content-bytes] #(byte-array (concat % block))))
      :else state)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TEST

;sends and receives packets into limits
;handle lost/out-of-sequence packs
;reads and writes a file
;TODO implement UDP connection

(defn connection-simulator []
  (atom []))

(defn send-packet [conn packet]
  (if (= (rand-int 2) 0)                              ;50%
    packet
    (when (= (rand-int 2) 0)                          ;25%
      (swap! conn conj packet)                        ;add pack to simu
      (get @conn (rand-int (dec (.length @conn))))))) ;return a lost pack, not the last added

(defn testa-transmissao-bytes [max-packet-size content-bytes]
  (let [conn1 (connection-simulator)
        conn2 (connection-simulator)
        result
        (loop [sender-state   (init-sender-state max-packet-size content-bytes)
               receiver-state (init-receiver-state max-packet-size)]
          (if-let [packet-to-receiver (packet-to-receiver sender-state)]
            (do
              (assert (<= (alength packet-to-receiver) max-packet-size))
              (let [packet-to-receiver (send-packet conn1 packet-to-receiver)
                    receiver-state     (if packet-to-receiver
                                         (receiver-handle packet-to-receiver receiver-state)
                                         receiver-state)
                    packet-to-xmitter  (send-packet conn2 (packet-to-xmitter receiver-state))
                    sender-state      (if packet-to-xmitter
                                         (xmitter-handle packet-to-xmitter sender-state)
                                         sender-state)]
                (recur sender-state receiver-state)))
            (:content-bytes receiver-state)))]
    (Arrays/equals ^bytes result ^bytes content-bytes)))

(defn read-file [path]
  (let [is    (-> ^String path File. FileInputStream.)
        bytes (byte-array (-> ^String path File. .length))]
    (.read is bytes)
    (.close is)
    bytes))

(defn testa-transmissao [path]
  (let [bytes (read-file path)]
    (time (testa-transmissao-bytes 1024 bytes))))

(defn testa-protocolo []
  (testa-transmissao "/home/melqui/Develop/GitHub/ByteCaverna/udp-spike/test/test.png")
  )


