(ns advent-of-code.day24
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(def ^:private ops {"AND" bit-and, "XOR" bit-xor, "OR" bit-or})

(defn- make-gate
  "Make the 'gate' vector from the elements of the line passed in"
  [line]
  (let [[a op b _ c] (str/split line #" ")]
    ;; To simplify some testing, we want a & b to be alphabetically ordered.
    (if (neg? (compare a b)) [a b op c] [b a op c])))

(defn- parse
  "Turn the blocks (start-state and gates) into a structural representation"
  [[state gates]]
  (let [state (into {} (map (fn [s]
                              (let [split (str/split s #": +")]
                                (hash-map (first split)
                                          (if (= (last split) "0") 0 1))))
                            (u/to-lines state)))
        gates (into [] (mapv make-gate (u/to-lines gates)))
        all   (into #{} (flatten (map #(list (first %) (second %) (nth % 3))
                                      gates)))]
    [(merge (zipmap all (repeat nil)) state) gates]))

(defn- solve
  "Work through the gates until everything has a value"
  [[state gates]]
  (loop [state state]
    (if (seq (filter nil? (vals state)))
      (recur (reduce (fn [state [a b op c]]
                       (if (and (nil? (state c)) (state a) (state b))
                         (assoc state c ((ops op) (state a) (state b)))
                         state))
                     state gates))
      state)))

(defn- answer
  "Derive the answer from the values in `state`"
  [state]
  (let [zs   (sort (filter #(str/starts-with? % "z") (keys state)))
        bits (map state zs)]
    (Long/parseLong (str/join "" (reverse bits)) 2)))

(defn part-1
  "Day 24 Part 1"
  [input]
  (->> input
       u/to-blocks
       parse
       solve
       answer))

(defn- bad-wiring
  "Determine if the passed-in gate spec is badly wired"
  [gate gates last-z]
  (let [[a b op c]       gate
        not-starts-with? (complement str/starts-with?)]
    (cond
      ;; Any AND op targeting a znn (other than z00) is an error
      (and (= op "AND")
           (str/starts-with? a "x")
           (str/starts-with? b "y")
           (str/starts-with? c "z")
           (not= c "z00"))
      true

      ;; Any XOR op should be either x/y targeting non-z, or non-x/y targeting z
      (and (= op "XOR")
           (not-starts-with? a "x")
           (not-starts-with? b "y")
           (not-starts-with? c "z")) true

      ;; Any zxx that isn't an XOR target (except the last znn) is an error
      (and (str/starts-with? c "z")
           (not= c last-z)
           (not= op "XOR"))          true

      ;; The result of xnn AND ynn should be an input to an "OR". Requires a
      ;; reduce-loop to check all of `gates`. Doesn't apply to "x00".
      (and (= op "AND")
           (str/starts-with? a "x")
           (str/starts-with? b "y")
           (not= a "x00"))           (reduce (fn [_ [a' b' op']]
                                               (if (and (= op' "OR")
                                                        (or (= c a') (= c b')))
                                                 (reduced false)
                                                 true)) true gates)

      ;; The result of xnn XOR ynn should be an input to an "AND". Also requires
      ;; a reduce-loop over `gates`. Note exception for "x00" again.
      (and (= op "XOR")
           (str/starts-with? a "x")
           (str/starts-with? b "y")
           (not= a "x00"))           (reduce (fn [_ [a' b' op']]
                                               (if (and (= op' "AND")
                                                        (or (= c a') (= c b')))
                                                 (reduced false)
                                                 true)) true gates)

      :else                          false)))

(defn- solve2
  "Find the eight gates that need to be rewired"
  [[state gates]]
  ;; Need to know the last "z" element, for a special-case comparison in the
  ;; `bad-wiring` fn. No reason to compute it there, for every loop iteration.
  (let [last-z (last (sort (keys state)))]
    (loop [[gate & gs] gates, wrong ()]
      (if (nil? gate)
        (str/join "," (sort wrong))
        (if (bad-wiring gate gates last-z)
          (recur gs (cons (gate 3) wrong))
          (recur gs wrong))))))

(defn part-2
  "Day 24 Part 2"
  [input]
  (->> input
       u/to-blocks
       parse
       solve2))
