(ns advent-of-code.day14
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- to-robots
  "Convert the four numbers to a robot representation"
  [[px py vx vy]]
  {:pos [px py], :vel [vx vy]})

(defn- move-robot
  "Move the given robot `n` times"
  [x y n r]
  (let [loc (:pos r)
        vel (:vel r)
        new (map + loc (mapv * vel [n n]))]
    [(mod (first new) x) (mod (last new) y)]))

(defn- move-robots
  "Move each robot `n` times in a field that is `x` by `y` in size"
  [x y n robots]
  (map #(move-robot x y n %) robots))

(defn- by-quad
  "Find the quadrant for the robot at `rx`,`ry`"
  [x y [rx ry]]
  (let [mid-x (quot x 2)
        mid-y (quot y 2)]
    (cond
      (and (< rx mid-x) (< ry mid-y)) 1
      (and (> rx mid-x) (< ry mid-y)) 2
      (and (< rx mid-x) (> ry mid-y)) 3
      (and (> rx mid-x) (> ry mid-y)) 4
      :else                          0)))

(defn- count-by-quadrant
  "Count the robots in each quadrant"
  [x y robots]
  (map #(count (last %))
       (rest (sort-by first
                      (group-by identity (map #(by-quad x y %) robots))))))

(defn part-1
  "Day 14 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map to-robots)
       (move-robots 101 103 100)
       (count-by-quadrant 101 103)
       (reduce *)))

(defn- has-line?
  "Take the `max-x` and `y` values and 'draw' the line, then check for 20 x's"
  [max-x y grid]
  (let [line (apply str
                    (for [x (range max-x)
                          :let [val (if (grid [x y]) "x" ".")]] val))]
    (str/includes? line "xxxxxxxxxxxxxxxxxxxx")))

(defn- move-robot2
  "Move the given robot `n` times"
  [x y n r]
  (let [loc (:pos r)
        vel (:vel r)
        new (map + loc (mapv * vel [n n]))]
    {:pos [(mod (first new) x) (mod (last new) y)], :vel vel}))

(defn- move-robots2
  "Move each robot `n` times in a field that is `x` by `y` in size"
  [x y n robots]
  (map #(move-robot2 x y n %) robots))

(defn- find-line
  "Look for a line of at least 20 contiguous points on a horizontal"
  [max-x max-y robots]
  (let [grid (set robots)]
    (loop [[y & ys] (range max-y)]
      (if (nil? y)
        false
        (if (has-line? max-x y grid)
          true
          (recur ys))))))

(defn- find-tree
  "Step the robots one second at a time until a straight line forms on an `x`"
  [x y robots]
  (loop [robots (move-robots2 x y 1 robots), seconds 1]
    (if (find-line x y (map :pos robots))
      seconds
      (recur (move-robots2 x y 1 robots) (inc seconds)))))

(defn part-2
  "Day 14 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map to-robots)
       (find-tree 101 103)))
