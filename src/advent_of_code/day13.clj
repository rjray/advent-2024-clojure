(ns advent-of-code.day13
  (:require [advent-of-code.utils :as u]))

(defn- find-solution
  "See if the settings for A and B can lead to the prize"
  [[ax ay bx by px py]]
  (let [sols (for [a (range 101), b (range 101) :when (and (= px (+ (* a ax)
                                                                    (* b bx)))
                                                           (= py (+ (* a ay)
                                                                    (* b by))))]
               (+ (* 3 a) b))]
    (if (seq sols)
      (apply min sols)
      0)))

(defn part-1
  "Day 13 Part 1"
  [input]
  (->> input
       u/to-blocks
       (map u/parse-out-longs)
       (map find-solution)
       (reduce +)))

(defn- augment-loc
  "Augment the two location values by 10000000000000 each"
  [[ax ay bx by px py]]
  (list ax ay bx by (+ 10000000000000 px) (+ 10000000000000 py)))

;; See https://en.wikipedia.org/wiki/Cramer%27s_rule
;;
;; This is a matter of solving a system of linear equations with the same
;; number of equations as there are unknowns.

(defn- determinant
  "Find the determinant of the two vectors"
  [u v]
  (- (* (first u) (last v)) (* (last u) (first v))))

(defn- find-solution2
  "Find the solution without brute force"
  [[ax ay bx by px py]]
  (let [ab (determinant [ax ay] [bx by])
        pb (determinant [px py] [bx by])
        ap (determinant [ax ay] [px py])]
    (if (= 0 (mod pb ab) (mod ap ab))
      (+ (* 3 (/ pb ab)) (/ ap ab))
      0)))

(defn part-2
  "Day 13 Part 2"
  [input]
  (->> input
       u/to-blocks
       (map u/parse-out-longs)
       (map augment-loc)
       (map find-solution2)
       (reduce +)))
