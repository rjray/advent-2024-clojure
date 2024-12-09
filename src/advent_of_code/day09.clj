(ns advent-of-code.day09
  (:require [advent-of-code.utils :as u]))

(def ^:private digits {\0 0, \1 1, \2 2, \3 3, \4 4, \5 5, \6 6, \7 7, \8 8
                       \9 9})

(defn- make-rep
  "Make the representation of the files"
  [chars]
  (let [pairs (partition 2 2 [0] (map digits chars))]
    (loop [[[blocks free] & pairs] pairs, id 0, rep ()]
      (if (nil? blocks)
        (vec rep)
        (recur pairs (inc id) (concat rep
                                      (repeat blocks id)
                                      (repeat free -1)))))))

(defn- find-last
  "Find the last element of `v` that is not -1"
  [v]
  (let [end (count v)]
    (reduce (fn [_ v']
              (when (< -1 (v v'))
                (reduced v')))
            0 (range (dec end) 0 -1))))

(defn- decompress
  "Do the decompression on the representation"
  [rep]
  (let [l-idx (- (count rep) 1)]
    (loop [[idx & is] (range l-idx), end (find-last rep), rep rep]
      (if (or (nil? idx) (< end idx))
        rep
        (if (= (rep idx) -1)
          (let [rep (assoc (assoc rep idx (rep end)) end -1)
                end (find-last rep)]
            (recur is end rep))
          (recur is end rep))))))

(defn- chksum
  "Compute the checksum"
  [rep]
  (reduce (fn [a idx]
            (if (pos? (rep idx)) (+ a (* idx (rep idx))) a))
          0 (range (count rep))))

(defn part-1
  "Day 09 Part 1"
  [input]
  (->> input
       u/to-lines
       first
       seq
       make-rep
       decompress
       chksum))

(defn- into-memory
  "Turn the 'compressed' representation into in-memory structure"
  [chars]
  (let [nums (map digits chars)
        ids  (interleave (range) (repeat -1))]
    (reduce (fn [a [id num]]
              (if (pos? num)
                (conj a (vec (repeat num id)))
                a))
            [] (partition 2 (interleave ids nums)))))

(defn- insert-into
  "Insert `file` into `slot`, only overwriting -1 values"
  [slot file]
  (loop [ret [], slot slot, file file]
    (if (seq file)
      (if (neg? (first slot))
        (recur (conj ret (first file)) (next slot) (next file))
        (recur (conj ret (first slot)) (next slot) file))
      (into ret slot))))

(defn- do-move
  "Move the `size` sectors of file at `idx` into free space at `slot`"
  [mem idx slot size]
  (assoc (assoc mem slot (insert-into (mem slot) (mem idx)))
         idx(vec (repeat size -1))))

(defn- try-move
  "Attempt to move the file at `idx` to an area of empty sectors"
  [idx mem]
  (let [size (count (mem idx))
        slot (loop [idx' 1]
               (if (= idx idx')
                 nil
                 (if (<= size (count (filter neg? (mem idx'))))
                   idx'
                   (recur (inc idx')))))]
    (if (nil? slot) mem (do-move mem idx slot size))))

(defn- defrag2
  "Run the second version of the de-frag algorithm"
  [mem]
  (loop [[idx & idxs] (range (dec (count mem)) 0 -1), mem mem]
    (cond
      ;; Note that we don't have to look at/consider index 0
      (nil? idx)                 mem
      ;; If looking at a slot that has any empty space, skip over it
      (some #(= -1 %) (mem idx)) (recur idxs mem)
      :else                      (recur idxs (try-move idx mem)))))

(defn- chksum2
  "Compute the checksum"
  [mem]
  (reduce (fn [a [x y]]
            (+ a (* x y)))
          0
          (filter #(pos? (last %)) (map-indexed vector (flatten mem)))))

(defn part-2
  "Day 09 Part 2"
  [input]
  (->> input
       u/to-lines
       first
       into-memory
       defrag2
       chksum2))
