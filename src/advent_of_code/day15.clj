(ns advent-of-code.day15
  (:require [advent-of-code.utils :as u]))

(def ^:private dirs {\^ [-1 0]
                     \> [0 1]
                     \v [1 0]
                     \< [0 -1]})

(defn- setup
  "Parse the input and set up the map and movement sequence"
  [input]
  (let [[field moves] (u/to-blocks input)
        field'        (u/to-matrix field)
        robot         (first (for [y (range (count field'))
                                   x (range (count (first field')))
                                   :when (= \@ (get-in field' [y x]))]
                               [y x]))]
    {:field (assoc-in field' robot \.)
     :robot robot
     :moves (map first (re-seq #"[<>v^]" moves))}))

(defn- move-if-can
  "The robot is attempting to move against a box. Move if it is possible."
  [field robot box dir]
  (loop [[s & ss] (iterate inc 1)]
    (let [pos' (mapv + box (mapv * dir [s s]))]
      (case (get-in field pos')
        \# [field robot] ;; Can't move
        \O (recur ss)    ;; Another box, keep going
        ;; This case means shift the 1+ box(es) in dir, then return the new
        ;; field and the initial `box` as the new robot position.
        \. [(assoc-in (assoc-in field pos' \O) box \.) box]))))

(defn- apply-move
  "Apply one move of the robot to the field, based on what is possible"
  [field robot move]
  (let [newpos (mapv + robot (dirs move))]
    (case (get-in field newpos)
      ;; Can't move the robot at all
      \# [field robot]
      ;; No obstruction, just move robot
      \. [field newpos]
      ;; Try moving any boxes in the way
      \O (move-if-can field robot newpos (dirs move)))))

(defn- run-moves
  "Run the sequence of moves over the robot adjusting boxes as apropos"
  [state]
  (let [{field :field
         robot :robot
         moves :moves} state]
    (loop [[m & ms] moves, field field, robot robot]
      (if (nil? m)
        field
        (let [[field' robot'] (apply-move field robot m)]
          (recur ms field' robot'))))))

(defn- score
  "Score the resulting field"
  [field]
  (reduce + (for [y (range (count field)), x (range (count (first field)))
                  :when (= \O (get-in field [y x]))]
              (+ (* 100 y) x))))

(defn part-1
  "Day 15 Part 1"
  [input]
  (->> input
       setup
       run-moves
       score))

(defn- expand
  "Expand the field along the X axis"
  [field]
  (let [expand {\# "##", \O "[]", \. "..", \@ "@."}]
    (mapv #(vec (apply str (map expand %))) field)))

(defn- setup2
  "Just like `setup` but expands the field"
  [input]
  (let [[field moves] (u/to-blocks input)
        field'        (expand (u/to-matrix field))
        robot         (first (for [y (range (count field'))
                                   x (range (count (first field')))
                                   :when (= \@ (get-in field' [y x]))]
                               [y x]))]
    {;;:field (assoc-in field' robot \.)
     :field field'
     :robot robot
     :moves (map first (re-seq #"[<>v^]" moves))}))

;; This algorithm adapted from Python from this Gist:
;; https://gist.github.com/lukemcguire/440899f3038b549315cfbcb7a4a79911

(defn- process-path
  "Process a kind of 'search' starting at the robot position, producing a stack
  of coordinates that should move."
  [field robot dir]
  (loop [path (list robot), stack (), visited #{}]
    (let [spot    (first path)
          path'   (rest path)]
      (cond
        (nil? spot)                     stack
        (= (get-in field spot) \#)      ()
        (or (visited spot)
            (= (get-in field spot) \.)) (recur path' stack visited)
        :else (let [visited' (conj visited spot)
                    stack'   (cons (list (get-in field spot)
                                         (first spot)
                                         (last spot)) stack)
                    path''   (cons (mapv + spot dir) path')
                    path''   (if (= (get-in field spot) \[)
                               (cons [(first spot) (inc (last spot))]
                                     path'')
                               path'')
                    path''   (if (= (get-in field spot) \])
                               (cons [(first spot) (dec (last spot))]
                                     path'')
                               path'')]
                (recur path'' stack' visited'))))))

(defn- do-move
  "Execute a single move, returning the new field and new robot position"
  [field robot dir]
  (let [[dy dx] dir
        stack   (process-path field robot dir)
        stack   (if (pos? dy)
                  (sort #(compare (second %2) (second %1)) stack)
                  (sort #(compare (second %1) (second %2)) stack))
        stack   (if (pos? dx)
                  (sort #(compare (last %2) (last %1)) stack)
                  (sort #(compare (last %1) (last %2)) stack))]
    (if (empty? stack)
      [field robot]
      [(reduce (fn [field [ch old-y old-x]]
                 (assoc-in (assoc-in field [(+ old-y dy) (+ old-x dx)] ch)
                           [old-y old-x] \.))
               field stack)
       (mapv + robot dir)])))

(defn- run-moves2
  "Run the simulation for part 2"
  [{field :field, robot :robot, moves :moves}]
  (loop [[move & moves] moves, field field, robot robot]
    (if (nil? move)
      field
      (let [[field' robot'] (do-move field robot (dirs move))]
        (recur moves field' robot')))))

(defn- score2
  "Score the resulting field (part 2)"
  [field]
  ;;(u/display field)
  (reduce + (for [y (range (count field)), x (range (count (first field)))
                  :when (= \[ (get-in field [y x]))]
              (+ (* 100 y) x))))

(defn part-2
  "Day 15 Part 2"
  [input]
  (->> input
       setup2
       run-moves2
       score2))
