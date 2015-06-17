(ns udp-spike.core
  (:import (java.util Arrays)
           (java.io File FileInputStream FileOutputStream)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TRANSMITTER:

(defn init-xmitter-state [max-packet-size content-bytes]
  {:max-packet-size  max-packet-size
   :content-bytes    content-bytes
   :block-identifier -128})

(defn add-identifier
  ([content]
   (byte-array (conj (vec content) 127)))
  ([content max-size identifier]
   (byte-array (conj (subvec (vec content) 0 (dec max-size)) identifier))))

(defn packet-to-receiver [xmitter-state]
  (if-let [content   (:content-bytes xmitter-state)]
    (let  [max-size  (:max-packet-size xmitter-state)
          identifier (:block-identifier xmitter-state)]
      (if (< (alength content) max-size)
        (add-identifier content)
        (add-identifier content max-size identifier)))))

(defn xmitter-handle [xmitter-state packet-from-receiver]
  (if (= (first packet-from-receiver) 127)
    (dissoc xmitter-state :content-bytes)
    (let [ret (update-in xmitter-state [:block-identifier] inc)
          max-size (:max-packet-size xmitter-state)]
      (update-in ret [:content-bytes] #(byte-array (subvec (vec %) (dec max-size)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; RECEIVER:

(defn init-receiver-state [max-packet-size]
  {:max-packet-size max-packet-size
   :content-bytes   (byte-array 0)
   :expected-packet -128
   :is-updated      false})

(defn packet-to-xmitter [receiver-state]
  (when (:is-updated receiver-state)
    (byte-array (vector (:expected-packet receiver-state)))))

(defn out-put-file [bytes]
  (let [file (File. "/home/melqui/Develop/Projects/udp-spike-testes/teste(c?pia).png")
        os (FileOutputStream. file)]
    (.write os bytes)
    (.close os)))

(defn receiver-handle [packet-from-xmitter receiver-state]
  (cond
    (= (last packet-from-xmitter) 127)                      ;last packet
    (let [ret   (assoc receiver-state :expected-packet 127)
          ret   (assoc ret :is-updated true)
          block (drop-last packet-from-xmitter)
          ret   (update-in ret [:content-bytes] #(byte-array (concat % block)))]
      (out-put-file (:content-bytes ret))
      ret)
    (= (last packet-from-xmitter) (:expected-packet receiver-state))
    (let [ret   (update-in receiver-state [:expected-packet] inc)
          ret   (assoc ret :is-updated true)
          block (drop-last packet-from-xmitter)]
      (update-in ret [:content-bytes] #(byte-array (concat % block))))
    :else (assoc receiver-state :is-updated false)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TESTE

; Depois pode ser transmitir via UDP mesmo.

(defn lost-packet-simu [packet-to-receiver]
  (if (= (mod (rand-int 2) 2) 0)                            ;50% chance to lost the pack
    packet-to-receiver
    (when (and (= (mod (rand-int 10) 3) 0)                  ;30% chance packs out of sequence
               (not= (last packet-to-receiver) -128))
      (byte-array (vector (dec (last packet-to-receiver)))))))

(defn testa-transmissao-bytes [max-packet-size content-bytes]
  (let [result
        (loop [receiver-state (init-receiver-state max-packet-size)
               xmitter-state (init-xmitter-state max-packet-size content-bytes)]
          (if-let [packet-to-receiver (packet-to-receiver xmitter-state)]
            (do
              (assert (<= (alength packet-to-receiver) max-packet-size))
              (let [packet-to-receiver (lost-packet-simu packet-to-receiver)
                    receiver-state (receiver-handle packet-to-receiver receiver-state)
                    xmitter-state (if (packet-to-xmitter receiver-state)
                                    (xmitter-handle xmitter-state (packet-to-xmitter receiver-state))
                                    xmitter-state)]
                (println receiver-state xmitter-state)
                (recur receiver-state xmitter-state)))
            (:content-bytes receiver-state)))]
    (Arrays/equals result content-bytes)))

(defn testa-transmissao [path]
  (let [file (File. path)
        bytes (byte-array (.length file))
        is (FileInputStream. file)]
    (.read is bytes)
    (.close is)
    (testa-transmissao-bytes 1024 bytes)))

(defn testa-protocolo []
  (testa-transmissao "/home/melqui/Develop/Projects/udp-spike-testes/teste.png")
  )
