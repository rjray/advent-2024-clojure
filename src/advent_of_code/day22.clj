(ns advent-of-code.day22
  (:require [advent-of-code.utils :as u]))

(defn- mix-prune
  "Do the mix and prune operations"
  [new secret]
  (bit-and (bit-xor new secret) 16777215))

(defn- next-num
  "Get the next number in sequence"
  [num]
  (as-> num $
    (mix-prune (bit-shift-left $ 6) $)
    (mix-prune (bit-shift-right $ 5) $)
    (mix-prune (bit-shift-left $ 11) $)))

(defn- get2000th
  "Get the 2000th iteration for `num`"
  [num]
  (second (take-nth 2000 (iterate next-num num))))

(defn part-1
  "Day 22 Part 1"
  [input]
  (->> input
       u/parse-out-longs
       (map get2000th)
       (reduce +)))

(defn- get-prices
  "Get the prices for the first 2000 secrets of `num`"
  [num]
  (map #(mod % 10) (take 2001 (iterate next-num num))))

(defn- get-diffs
  "Get price diffs over the given list"
  [prices]
  (map vec (partition 4 1 (map #(apply - (reverse %)) (partition 2 1 prices)))))

(defn- diffs-to-price
  "Create the diffs vectors and map each to the price at that point"
  [prices]
  (map list (get-diffs prices) (drop 4 prices)))

(defn- scan-mapping
  "Scan the diff/price mappings for one monkey"
  [memo pairs]
  (loop [[[diff price] & ps] pairs, memo memo, seen #{}]
    (if (nil? diff)
      memo
      (if (seen diff)
        (recur ps memo seen)
        (recur ps (update memo diff (fnil + 0) price) (conj seen diff))))))

(defn part-2
  "Day 22 Part 2"
  [input]
  (->> input
       u/parse-out-longs
       (map get-prices)
       (map diffs-to-price)
       (reduce (fn [memo mapping]
                 (scan-mapping memo mapping)) {})
       vals
       (apply max)))
