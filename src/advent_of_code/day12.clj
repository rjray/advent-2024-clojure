(ns advent-of-code.day12
  (:require [advent-of-code.utils :as u]
            [clojure.set :as set]))

(def ^:private dirs [[-1 0], [0 1], [1 0], [0 -1]])

(defn- surrounding
  "Get the 4 coordinates surrounding `node`"
  [node]
  (reduce (fn [ms mv] (cons (mapv + node mv) ms))
          () dirs))

(defn- find-nodes
  "Find adjacent nodes that have the same crop and are available"
  [avail seen crop node m]
  (filter #(and (avail %)
                (= crop (get-in m %))
                (not (seen %))) (surrounding node)))

(defn- find-region
  "Find a single contiguous region in `m` based on `avail` coordinates"
  [avail m]
  (let [start (first avail)
        crop  (get-in m start)
        queue (into clojure.lang.PersistentQueue/EMPTY [start])]
    (loop [queue queue, used #{}, avail avail, seen #{start}]
      (let [node (peek queue), queue (pop queue)]
        (if (nil? node)
          [{:crop crop, :used used, :area (count used)} avail]
          (let [new-nodes (find-nodes avail seen crop node m)]
            (recur (into queue new-nodes)
                   (conj used node)
                   (disj avail node)
                   (reduce conj seen new-nodes))))))))

(defn- find-regions
  "Take the map given in `m` and break down all the regions"
  [m]
  (let [max-y  (count m)
        max-x  (count (first m))
        coords (for [y (range max-y), x (range max-x)] [y x])
        locs   (set coords)]
    (loop [regions (), avail locs]
      (if (nil? (seq avail))
        regions
        (let [[new-region new-avail] (find-region avail m)]
          (recur (cons new-region regions) new-avail))))))

(defn- find-perimeter
  "Find the perimeter of the region `r`"
  [r]
  ;; Each block has 4 sides. Any sides not in :used add to the perimeter.
  (let [used        (:used r)
        open-edges  (for [node used] (count (filter used (surrounding node))))
        total-edges (reduce + open-edges)
        perimeter   (- (* 4 (:area r)) total-edges)]
    (assoc r :perimeter perimeter)))

(defn- calc-cost
  "Calculate the fencing cost for `r`"
  [r]
  (* (:area r) (:perimeter r)))

(defn part-1
  "Day 12 Part 1"
  [input]
  (->> input
       u/to-matrix
       find-regions
       (map find-perimeter)
       (map calc-cost)
       (reduce +)))

(defn- innermost-loop
  "The inner-most loop for the algorithm, basically a while-loop."
  [used x y dx dy]
  (loop [cx x, cy y]
    (let [p1 [(+ cy dx) (+ cx dy)]
          p2 [(+ cy dy) (+ cx dx)]]
      (if (and (used p1) (not (used p2)))
        (recur (+ cx dy) (+ cy dx))
        [cy cx]))))

(defn- inner-loop
  "The inner loop of the side-counting algorithm. Loops over elements of :used."
  [used x y sides visited]
  (loop [[[dy dx] & ds] dirs, sides sides, visited visited]
    (if (nil? dx)
      [sides visited]
      (let [tx (+ x dx), ty (+ y dy)]
        (if (not (used [ty tx]))
          (let [[cy cx] (innermost-loop used x y dx dy)
                visit   [cy cx dy dx]]
            (if (visited visit)
              (recur ds sides visited)
              (recur ds (inc sides) (conj visited visit))))
          (recur ds sides visited))))))

(defn- find-sides
  "Find the number of sides to region `r`"
  [r]
  (loop [[[y x] & used] (:used r), sides 0, visited #{}]
    (if (nil? y)
      (assoc r :perimeter sides)
      (let [[sides' visited'] (inner-loop (:used r) x y sides visited)]
        (recur used sides' visited')))))

(defn part-2
  "Day 12 Part 2"
  [input]
  (->> input
       u/to-matrix
       find-regions
       ;; This "cheats" by storing the value on :perimeter so `calc-cost` works
       (map find-sides)
       (map calc-cost)
       (reduce +)))
