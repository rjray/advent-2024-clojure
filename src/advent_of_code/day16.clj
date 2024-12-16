(ns advent-of-code.day16
  (:require [advent-of-code.utils :as u]))

(def ^:private dirs {:n [-1 0]
                     :e [0 1]
                     :s [1 0]
                     :w [0 -1]})
(def ^:private turn {:n [:w :e]
                     :s [:e :w]
                     :e [:n :s]
                     :w [:s :n]})

(defn- setup
  "Turn the input into a matrix and locate start/end"
  [input]
  (let [maze  (u/to-matrix input)
        max-y (count maze)
        max-x (count (first maze))
        start (first (for [y (range max-y), x (range max-x)
                           :when (= \S (get-in maze [y x]))]
                       [y x]))
        end   (first (for [y (range max-y), x (range max-x)
                           :when (= \E (get-in maze [y x]))]
                       [y x]))]
    {:maze maze, :start start, :end end}))

(defn- moves-from
  "Find moves from the square in `node`, including turns if possible"
  [node maze]
  ;; There are only ever three possible moves: forward, turn left, turn right.
  ;; Add any that aren't faced with a wall.
  (let [[loc dir path cost] node
        fw [(mapv + loc (dirs dir)) dir (cons loc path) (inc cost)]
        lt [loc ((turn dir) 0) (cons loc path) (+ 1000 cost)]
        rt [loc ((turn dir) 1) (cons loc path) (+ 1000 cost)]
        moves (list lt rt)]
    (if (= \# (get-in maze (first fw)))
      moves
      (cons fw moves))))

(defn- bfs
  "Do searching, returning all paths/cost between `start` and `end`"
  [{:keys [maze start end]}]
  (let [queue (list [start :e () 0])]
    (loop [[node & queue] queue, seen #{}]
      (if (nil? node)
        -1
        (if (= (first node) end)
          (last node)
          (let [moves (filter #(not (seen (vec (take 2 %))))
                              (moves-from node maze))]
            (recur (sort-by last (concat queue moves))
                   (conj seen (vec (take 2 node))))))))))

(defn part-1
  "Day 16 Part 1"
  [input]
  (->> input
       setup
       bfs))

(defn- bfs2
  "Do searching, returning all paths/cost between `start` and `end`"
  [{:keys [maze start end]}]
  (let [queue (list [start :e () 0])]
    (loop [[node & queue] queue, seen #{}, sols ()]
      (if (nil? node)
        (sort-by last sols)
        (if (= (first node) end)
          (recur queue seen (cons node sols))
          (let [moves (filter #(not (seen (vec (take 2 %))))
                              (moves-from node maze))]
            (recur (sort-by last (concat queue moves))
                   (conj seen (vec (take 2 node)))
                   sols)))))))

(defn- find-seats
  "Find the total number of (unique) seats along all shortest paths"
  [sols]
  ;; Shortest ones start at the front
  (let [short-len (last (first sols))
        shortests (filter #(= short-len (last %)) sols)]
    (inc (count (reduce (fn [seats sol]
                          (into seats (sol 2)))
                        #{} shortests)))))

(defn part-2
  "Day 16 Part 2"
  [input]
  (->> input
       setup
       bfs2
       find-seats))
