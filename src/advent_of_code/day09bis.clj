(ns advent-of-code.day09bis
  (:require [advent-of-code.utils :as u]))

(def ^:private digits {\0 0, \1 1, \2 2, \3 3, \4 4, \5 5, \6 6, \7 7, \8 8
                       \9 9})

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

(defn- free-sectors
  "Overwrite data sectors with -1, from the end"
  [file num]
  (vec (reverse (loop [ret [], file (reverse file), num num]
                  (if (pos? num)
                    (if (neg? (first file))
                      (recur (conj ret -1) (next file) num)
                      (recur (conj ret -1) (next file) (dec num)))
                    (into ret file))))))

(defn- move-sectors
  "Move the last sectors in `file` to the open space in `slot`"
  [mem file slot]
  (let [slot'      (mem slot)
        file'      (mem file)
        free-space (count (filter neg? slot'))
        file-size  (count (filter pos? file'))]
    (if (<= file-size free-space)
      (assoc mem
             slot (insert-into slot' (take file-size file'))
             file (free-sectors file' file-size))
      (assoc mem
             slot (insert-into slot' (take free-space file'))
             file (free-sectors file' free-space)))))

(defn- defrag-by-sector
  "Run the de-frag algorithm for part 1"
  [mem]
  (loop [idx (dec (count mem)), mem mem]
    (cond
      ;; Don't have to look at/consider index 0
      (zero? idx)                  mem
      ;; If looking at a slot that has only empty space, skip over it
      (every? #(neg? %) (mem idx)) (recur (dec idx) mem)
      ;; This slot has at least one non-zero sector. Try to find at least one
      ;; open slot.
      :else
      (let [slot (loop [idx' 1]
                   (if (>= idx' idx)
                     0
                     (if (some #(neg? %) (mem idx'))
                       idx'
                       (recur (inc idx')))))]
        (if (pos? slot)
          ;; We can move at least one sector into (mem slot). Recur with `idx`
          ;; unchanged to see if there are more sectors in `idx` to move.
          (recur idx (move-sectors mem idx slot))
          ;; If no spare space was found before running into the other index,
          ;; we're finished. Signal that by forcing `idx` to 0.
          (recur 0 mem))))))

(defn- chksum
  "Compute the checksum"
  [mem]
  (reduce (fn [a [x y]] (+ a (* x y)))
          0
          (filter #(pos? (last %)) (map-indexed vector (flatten mem)))))

(defn part-1
  "Day 09 Part 1"
  [input]
  (->> input
       u/to-lines
       first
       into-memory
       defrag-by-sector
       chksum))

(defn- do-move
  "Move the `size` sectors of file at `idx` into free space at `slot`"
  [mem idx slot size]
  (assoc (assoc mem slot (insert-into (mem slot) (mem idx)))
         idx (vec (repeat size -1))))

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

(defn- defrag-by-file
  "Run the second version of the de-frag algorithm"
  [mem]
  (loop [[idx & idxs] (range (dec (count mem)) 0 -1), mem mem]
    (cond
      ;; Note that we don't have to look at/consider index 0
      (nil? idx)                 mem
      ;; If looking at a slot that has any empty space, skip over it
      (some #(neg? %) (mem idx)) (recur idxs mem)
      :else                      (recur idxs (try-move idx mem)))))

(defn part-2
  "Day 09 Part 2"
  [input]
  (->> input
       u/to-lines
       first
       into-memory
       defrag-by-file
       chksum))
