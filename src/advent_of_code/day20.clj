(ns advent-of-code.day20
  (:require [advent-of-code.utils :as u]))

(def ^:private dirs [[-1 0] [0 1] [1 0] [0 -1]])

(defn- setup
  "Turn the input into a matrix and locate start/end"
  [input]
  (let [maze  (u/to-matrix input)
        max-y (count maze)
        max-x (count (first maze))
        start (first (for [y (range max-y), x (range max-x)
                           :when (= \S (get-in maze [y x]))]
                       [y x]))
        end (first (for [y (range max-y), x (range max-x)
                         :when (= \E (get-in maze [y x]))]
                     [y x]))]
    {:maze (-> maze
               (assoc-in start \.)
               (assoc-in end \.)),
     :start start, :end end}))

(defn- next-move
  "Return the next move from the current position"
  [node maze cost]
  (first (for [dir dirs
               :let [node' (mapv + node dir)]
               :when (= \. (get-in maze node'))
               :when (nil? (get cost node'))]
           node')))

(defn- find-full-cost
  "Find the full cost of the maze, creating a map of squares to cost"
  [state]
  (let [{:keys [maze start end]} state]
    (loop [node end, cur 0, cost {}]
      (if (= node start)
        (-> state (assoc :costs (assoc cost node cur)) (assoc :cost cur))
        (recur (next-move node maze cost)
               (inc cur)
               (assoc cost node cur))))))

(defn- find-jumps
  "Find possible jumps (0-2) from the current spot"
  [node cost maze costs visited total]
  (for [dir dirs
        :let [node'  (mapv + node dir)
              node'' (mapv + node dir dir)]
        :when (= \# (get-in maze node'))
        :when (= \. (get-in maze node''))
        :when (not (visited node''))]
    (hash-map [node' node''] (- total (+ 2 cost (costs node''))))))

(defn- find-cheats
  "Find all cheats and their savings over the final path-cost"
  [state]
  (let [{:keys [maze start end costs cost]} state]
    (loop [node start, cur 0, known {}, visited #{}]
      (if (= node end)
        (assoc state :shortcuts known)
        (let [move (next-move node maze visited)
              jumps (find-jumps node cur maze costs visited cost)]
          (recur move (inc cur) (into known jumps) (conj visited node)))))))

(defn part-1
  "Day 20 Part 1"
  [input]
  (->> input
       setup
       find-full-cost
       find-cheats
       :shortcuts
       vals
       (filter #(>= % 100))
       count))

(defn- find-jumps-20
  "Find possible jumps within a `limit`-square radius"
  [node rempath limit]
  (for [node' (drop limit rempath)
        :let [dist (u/manhattan-dist node node')]
        :when (<= 2 dist limit)]
    [node' dist]))

(defn- find-cheats-20
  "Find all cheats and their savings, when the trick has a 20-space range"
  [state]
  (let [{:keys [costs]} state
        path            (reverse (map first (sort-by last costs)))
        pathlen         (dec (count path))]
    (loop [cnt 0, picos 0, path path]
      (if (seq (drop 20 path))
        (let [[node & rempath] path
              jumps            (find-jumps-20 node rempath 20)
              newcnt           (reduce (fn [acc [pt dist]]
                                         (if (<= (+ picos dist (costs pt))
                                                 (- pathlen 100))
                                           (inc acc)
                                           acc))
                                       cnt jumps)]
          (recur newcnt (inc picos) rempath))
        cnt))))

(defn part-2
  "Day 20 Part 2"
  [input]
  (->> input
       setup
       find-full-cost
       find-cheats-20))
