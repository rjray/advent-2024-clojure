# Breakdown of Files

Jump to day: [1](#day01clj)&nbsp;|&nbsp;[2](#day02clj)&nbsp;|&nbsp;[3](#day03clj)&nbsp;|&nbsp;[4](#day04clj)&nbsp;|&nbsp;[5](#day05clj)&nbsp;|&nbsp;[6](#day06clj)&nbsp;|&nbsp;[7](#day07clj)&nbsp;|&nbsp;[8](#day08clj)&nbsp;|&nbsp;[9](#day09clj)&nbsp;|&nbsp;[10](#day10clj)&nbsp;|&nbsp;[11](#day11clj)&nbsp;|&nbsp;[12](#day12clj)&nbsp;|&nbsp;[13](#day13clj)&nbsp;|&nbsp;[14](#day14clj)&nbsp;|&nbsp;[15](#day15clj)&nbsp;|&nbsp;[16](#day16clj)&nbsp;|&nbsp;[17](#day17clj)&nbsp;|&nbsp;[18](#day18clj)&nbsp;|&nbsp;[19](#day19clj)&nbsp;|&nbsp;[20](#day20clj)&nbsp;|&nbsp;[21](#day21clj)&nbsp;|&nbsp;[22](#day22clj)&nbsp;|&nbsp;[23](#day23clj)&nbsp;|&nbsp;[24](#day24clj)&nbsp;|&nbsp;[25](#day25clj)

Here is a breakdown of the various files in this directory. Files with names of
the form `dayNN.clj` represent the code actually used to solve the problems
(with some tweaking done using a static analysis plug-in for Leiningen). Files
with `bis` in the name are modified/tuned versions of the given original day.
(If you see comments in a file, I can usually promise you they were added after
the fact.)

The numbers in parentheses in the descriptions of the files represent the rank
I had for when my solutions were submitted and accepted. Time, if given, is a
rough estimate of how long it took to solve both halves.

A given day and part can be run via:

```
lein run DAY PART
```

where `DAY` is a number from 1-25 and `PART` is 1 or 2. If there is a "bis"
version of a day, that can be run via:

```
lein run -b DAY PART
```

## [day01.clj](day01.clj)

Day 1 (7796/7184, 34:21).

(Note: Started this pair at about 10 minutes past to opening of the puzzle. The
time shown is the AoC time, my time was about 24:00.)

Both halves of this first day were well-suited to Clojure. Part 1 was to
compare to sorted lists of numbers and calculate the difference between each
pair. These numbers (using `Math/abs` to keep them all positive) were then
summed up with `reduce`.

Part 2 was actually easier than part 1. It involved calculating a "similarity"
score by taking each number in the first list and scaling it out by how often
it occurs in the second list. The `frequencies` built-in did the hard work of
setting up the table of counts for each member of the second list, then a `map`
over the first list produced the "similarities".

## [day02.clj](day02.clj)

Day 2 (6283/6110, 39:54).

Another slightly-late start, pulled up the puzzle after 5 minutes. My time was
closer to 35:00.

Part 1 was *really* suited to Clojure, as determining the first condition on
the list of numbers was a matter of using `apply` and `<=`/`>=`. The second
condition was a little trickier, but `partition` and `every?` did the trick.

Part 2 was harder, though. Where part 1 took about 13:17 to solve, getting part
2 done was another 21:37. The problem was trying to get Clojure to drop an
arbitrary n^th element from a list. Once that was working, the rest fell into
place. The `drop-nth` function will probably get added to the utils.

## [day03.clj](day03.clj)

Day 3 (3174/4105, 22:06).

This was actually very easy, though something weird made part 2 take about 5
minutes longer than it should have.

Part 1 was to find all "`mul`" instructions, evaluate them, and sum the
results. Finished in 8:08, much of which was wrestling with escaping characters
in a Clojure regular expression literal.

Part 2 introduced to "control" instructions that would toggle a Boolean state
which in turn controlled whether a given `mul` should be evaluated or skipped.
This should have taken less than 5 additional minutes, using `case`. But the
`case` block kept evaluating the "default" arm even after matching one of the
first two arms. I couldn't figure out why, so I just rewrote it with `cond` and
it worked right the first try.

Need to see what was wrong with the `case` block.

## [day04.clj](day04.clj)

Day 4 (6725/5818, 45:49).

An interesting pair of puzzles. Both were "word search" puzzles in a field of
letters.

For part 1, you had to find all occurrences of `XMAS` in the field, in any of
the eight directions, with overlapping allowed. My first pass at this got the
wrong answer on the test data due to not looking to see if a given `X` led to
more than one string. Fixed that and got it right.

Part 2 was a little trickier, but only took an additional ~12 minutes from the
initial 33 for part 1. Here, you had to find _an_ "X" made from two occurrences
of `MAS` that shared the `A`. The algorithm was similar, and actually a little
easier since a given `A` could only add 1 to the overall count.

## [day05.clj](day05.clj)

Day 5 (11943/9215, 1:24:45).

This was way harder for me than it should have been. I flailed much too long.

More later. Maybe.

## [day06.clj](day06.clj)

Day 6 (16863/10964, 3:28:40).

Due to being out to dinner with friends, I started this one 2:16:00 late. So
the time actually spent on it was roughly 1:12:40.

Part 1 took only 14 minutes, and was a simple matter of tracking the guard's
steps in a set object until the guard stepped off the field.

Part 2 involved finding a cycle, and I did a poor job. Once I finally got the
code to not be stuck in a loop and get the right answer for the test data, the
first two attempts on the puzzle data were too high. The third try got it
right, but clearly my method for detecting the cycle is sub-optimal. Will
compare this to other Clojure solutions tomorrow.

## [day06bis.clj](day06bis.clj)

Day 6 revisited. After finishing part 2 last night, I realized that I could
detect the cycles much faster by looking for the recurrence of a grid space
being moved into by the same direction. This morning, I implemented it but it
didn't initially work. After some fruitless debugging, I took a look at
[this solution from Clojurians
Slack](https://gitlab.com/maximoburrito/advent2024/-/blob/main/src/day06/main.clj).
I found the problem with my code (this person had implemented the same approach
I thought of, he just got it the first time around), and also found that I
could optimize it tremendously by only considering the open grid-points that
had been visited by the guard in part 1. This took the run-time of part 2 down
from 205 seconds to 47.8 seconds.

## [day07.clj](day07.clj)

Day 7 (4644/4236, 34:10).

This was another case of puzzles well-suited to Clojure. The goal was to
evaluate a sequence of numbers with different possible combinations of infix
operators, to see if the result matched a given value. Clojure's ability to
throw around functions as data, specifically the functions `+` and `*`, made
this pretty straightforward.

Part 1 was just to find all the lines that checked out when having the choice
of `+` and `*` for operators. A vector of these two, and the `selections`
function from the combinatorics library generated all the possible permutations
for a given total number of operators. An eval-like function to take these and
calculate the value was easy to write. Answer came back in 2 seconds.

Part 2 upped the ante by adding a third "operator": concatenation (referred to
with `||`). This would concatenate two numbers into one, i.e., `[12 657]` would
become `12657`. Writing the operator and adding it to the a new vector of
operators was easy. I decided to replicate most of the support functions to
assume three operators rather than trying to generalize everything. Getting the
answer took 107.9 seconds.

I will revisit this tomorrow. I want to have just one set of
`do-eval`/`evaluate` that takes the operators as a parameter. I might also look
at evaluating lines in parallel to cut the run-time down.

## [day07bis.clj](day07bis.clj)

Revisit of day 7.

Rewritten so that there is just one version each of the `evaluate` and
`do-eval` functions. The only other functions are the runners (`part-1` and
`part-2`) and the number-concatenation function. Originally it was called
`int-concat`, but in this file I just called it `||` outright because Clojure
can do that.

There's no optimization in this (yet). The run-time for part 2 was about 7s
faster than the original, because I corrected an oversight in the concatenation
function (was using an `apply` that I didn't need). But the code went from 70
lines to 46, a 34% reduction.

## [day08.clj](day08.clj)

Day 8 (6203/6206, 1:05:58).

Got home from a hockey _right at the time_ the puzzle unlocked.

This was a little challenging, mostly because I got the signs wrong the first
time around on part 1. I had to do some pencil/paper work to figure that out.
Once I had that, part 1 was done and correct (in 47:37).

Part 2 was tricky in that I had been using a `for`-comprehension in part 1, but
for this I needed to go as far in each slope-direction as I could until going
out of bounds. I replaced the `for` with a `loop` construct that would exit
when both directions were off the map. This had the bonus of reusing everything
_else_ from part 1, so I only had to write one new function and slightly modify
one other.

## [day09.clj](day09.clj)

Day 9 (13877/28950, 15:19:48).

Started at 10:52:00. Part 1 finished at 11:55:24. Stopped at 12:30AM.
Restarted at about 8:30AM, but worked on it in 5-6 minute "rests" from the
day-job. So part 1 took about 1:03:24, and part 2's total time is unknown.

Part 1 was done very poorly, and required increasing the stack-size of the JVM
process in order to complete. I will leave this in place, since this is the
code that generated the submitted answer.

Part 2 was brutal, mainly because the data representation for part 1 made it
impossible to re-use anything. Looking at how others represented their data, I
was able to re-do that part, and from there I eventually got it working.

I'll revisit this and re-do part 1 using the representation logic from part 2,
and see if I can get it to run faster. In this code, part 1 takes about 65-70
seconds, but part 2 ran in 12.95.

## [day09.clj](day09.clj)

Re-did part 1 using the logic from part 2, but it ran slower. Not every
experiment works out.

## [day10.clj](day10.clj)

Day 10 (8234/7774, 1:10:27).

Started at 9:16:00.

Part 1 took a little longer than a simple search problem should have, as I
forgot to properly set up the instance of `PersistentQueue/EMPTY`. Once I fixed
that, I was getting the wrong numbers for the test input. I finally figured out
that I was counting a given `9` more than once, if there was more than one way
to reach it from a given trailhead. I corrected that, and had the first part
correct in (roughly) 49:51.

Then came part 2, where you have to count all the distinct paths you can make
from a given trailhead. Just as I was wondering how to approach this, I
realized that the per-trailhead numbers for the test input were *exactly the
ones I had gotten in error in part 1*. So, all I had to do was figure out a
clean way to handle both cases without copy/pasting 80% of the part 1 code.
Part 2 was done in an additional 4:36.

## [day11.clj](day11.clj)

Day 11 (--/--).

## [day12.clj](day12.clj)

Day 12 (--/--).

## [day13.clj](day13.clj)

Day 13 (--/--).

## [day14.clj](day14.clj)

Day 14 (--/--).

## [day15.clj](day15.clj)

Day 15 (--/--).

## [day16.clj](day16.clj)

Day 16 (--/--).

## [day17.clj](day17.clj)

Day 17 (--/--).

## [day18.clj](day18.clj)

Day 18 (--/--).

## [day19.clj](day19.clj)

Day 19 (--/--).

## [day20.clj](day20.clj)

Day 20 (--/--).

## [day21.clj](day21.clj)

Day 21 (--/--).

## [day22.clj](day22.clj)

Day 22 (--/--).

## [day23.clj](day23.clj)

Day 23 (--/--).

## [day24.clj](day24.clj)

Day 24 (--/--).

## [day25.clj](day25.clj)

Day 25 (--/--).
