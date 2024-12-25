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

## [day09bis.clj](day09bis.clj)

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

Day 11 (5229/26916, 11:45:16).

Started on time, stopped at 11:37:31. Resumed at 7:30:00 or so. Finished at
8:45:16. Part 1 took 19:11, part 2 took... about 3:33:36 in total.

Part 1 was easily brute-forced, just a `map` over existing stones to replace
them with new ones, done `n` times, then a `count` on the final list.

Part 2 was an embarrassment. I figured out that I just need to keep a count of
each stone-number each cycle, and use those to create a new mapping of
stone-number to count. But I kept getting wrong answers when compared to the
test input or when comparing the new algorithm (run for 25 blinks) to the part
1 answer. Initially, my part 2 numbers were too high (and the gap grew as
iterations increased). I tried adjusting how the counts were incremented and
ended up with answer that were too low.

Debugging this was hard, since the size of the set of numbers grew relatively
fast between iterations. At one point, I basically abandoned all the code (but
not the algorithm) for part 2 and rewrote it. Now, I was still getting low
answers but they were much closer than before. I *finally* managed to trace it
to a case where a stone-number has an even number of digits, and the two split
numbers are the same. In these cases, I would only count the new number once,
not twice. Finally, I had the correct answer.

Part 2 runs a lot faster than part 1 does, despite having 3x the iterations
(284ms vs. 762ms). While it would be trivial to re-do part 1 using the part 2
algorithm, I'm just sick of this puzzle.

## [day12.clj](day12.clj)

Day 12 (7220/4210, 1:59:18).

Started 4 minutes late due to band.

Part 1 went OK on the test data but had an infinite loop on the puzzle data.
Found that it was in the region-detection code and fixed it with an additional
`set` value to track the seen elements separately from the elements being added
to the region itself. It took just over 1:09:00 to finish, given the debugging
needed. Run-time was 285.6ms.

Part 2 was much harder. After reading some comments on reddit, I decided to go
with an approach of "collapsing" sides/squares to see if they followed a
straight line and qualified as a distinct side. Tested the hell out of this one
because I wasn't at all confident in it. It passed all the test-data values, so
I ran it on the puzzle data and got the right answer in 311.8ms.

## [day13.clj](day13.clj)

Day 13 (695/2907, 1:01:45).

Part 1 was brute-forced, and landed me in the top 1000 for the first time in
quite a while. It was just a nested a/b loop in a `for` comprehension, where
the loop returned all costs of combinations of a/b that hit the mark, then
applied `min` to that list.

Part 2, as expected, upped the ante severely. The X/Y of every prize had a huge
number added to it (10^13), and that was impossible to brute-force, even when I
tried to utilize some modulo-math to reduce the search-space. Then I realized I
was looking at a system of linear equations, and started googling for linear
algebra solutions. I came across
[Cramer's Rule](https://en.wikipedia.org/wiki/Cramer%27s_rule), and was able to
code a solution in a little over 45 minutes (after part 1 took only 10:49). Of
course, part 2 being the RIGHT way to do it, finishes much faster than part 1.

## [day14.clj](day14.clj)

Day 14 (9141/7683, 3:14:41).

Started at roughly 10:45:00, so this would have taken about 1:19:00 or so.

Part 1 was simple modulo math, no big deal.

Part 2 required recognizing when a formation occurred. I had no idea how to do
this, so I turned to reddit. But before I could crib from anyone else's
solution, I saw a rather impressive visualization of part 2 from someone. When
I saw that the tree was framed by a border, I realized that I only needed to
detect some number of contiguous occupied squares on a y-line. I picked 20 as
an arbitrary number to look for. I got the correct answer first try. But I
spent way too long chasing a `NullPointerException` error before I finally
found it. I estimate I lost 20 minutes or so on the debugging, there.

## [day15.clj](day15.clj)

Day 15 (7026/18252, 16:50:52).

Started at 10:15:00 due to hockey game. Stopped at 1:00AM to sleep, resumed at
9:25AM. Finished roughly 1:50:52.

Part 1 went well-enough. Finished in just under 40 minutes.

Part 2 was a nightmare for some reason. It ended up being incredibly long and
complicated in terms of code, and in the end it didn't work. All the test sets
got the correct answers, but not the actual puzzle data (my second wrong answer
of the year). I had found the "last" bug by stepping through dumps of the field
at each move, from the larger example, looking for the place where a move
wasn't executed correctly. But I couldn't do that with the real data. By this
time it was almost 1:00PM and I was tired of the puzzle. I found a rather
clever approach that was a sort of search-space problem, written in Python, and
adapted that. The source of that was: https://gist.github.com/lukemcguire/440899f3038b549315cfbcb7a4a79911

## [day16.clj](day16.clj)

Day 16 (4919/3332, 1:57:57).

Another breadth-first search problem, hampered by my brains seeming
unwillingness to remember how to do this. Part 1 took over 1.5 hours because of
the bad brain, but part 2 only took an additional 26:20. Both solutions were
slow as hell, though. Sometimes I feel like the only person who has performance
problems with Clojure, probably because I run the solutions through `lein`.

But at least I got through both parts of today without looking at anyone else's
code or commentary.

## [day17.clj](day17.clj)

Day 17 (2923/9852, 12:45:17).

Started on time, finished part 1 in 39:43. Part 2 twisted my brain (I'm not
good at reverse-engineering asm code, nor am I good at reverse-engineering lots
of bit-operations). I worked on it until 11:30 my time (2:30:00 into the
puzzle) and went to sleep. A few hints from one of the people (Norman) on the
AoC channel of the Clojure slack helped me figure out that part of my problem
was looking at the wrong end of program data when comparing it to partial
solutions. Since I resumed right at 9:00AM and finished at 9:45:17, the total
actual working-time for part 2 would be around 2:15:34 or so.

Oddly, my code threw a `StackOverflow` error on the test data. I took a chance
running it on the puzzle data and then trying that answer. The risk paid off,
but I'm curious why the smaller data-set caused some sort of recursion loop.

## [day18.clj](day18.clj)

Day 18 (8495/8191, 3:02:09).

Started at roughly 10:40PM (hockey game). Finished part 1 about 1:04:47, due
mostly to a well-hidden bug in my BFS implementation.

Part 2 was surprisingly easier than I was expecting. We were told to find the
first of the remaining blocks that would completely block any route out of the
field. CLojure's `reduce` worked wonders here, both progressively adding blocks
to the field and iterating over the remaining "bytes". That solution came in
just over 16 additional minutes. It's kind of brute-force-ish, but it worked.

## [day19.clj](day19.clj)

Day 19 (22345/19722, 17:40:19).

Started about 8 minutes late. Gave up around 11:25PM without getting part 1
done. Finally got things working after looking at some other solutions.

## [day20.clj](day20.clj)

Day 20 (5654/14281, 16:46:36).

Part 1 finished in 2:14:41, stopped for the night at that point. Resumed at
8:00AM, stopped again at 9:00 for work. Picked at it off and on, finished about
1:46.

Part 1 was horribly delayed by a series of really dumb errors on my part.

Part 2 got messy when I tried to do BFS from each path-point to get possible
"jumps". In the end, I studied
https://github.com/zelark/AoC/blob/master/src/zelark/aoc_2024/day_20.clj and
used a Manhattan Distance approach.

I can probably re-write part 1 using part 2, and parameterize some of the
hard-coded numbers. If I feel like it.

## [day21.clj](day21.clj)

Day 21 (16217/12427, unknown).

Due to a variety of issues, mostly around holiday preparation, I wasn't able to
complete this day until late into the 23rd. On top of that, I was wholly unable
to get anything beyond a brute-force algorithm put together (and it didn't even
work). Even if I got it working, I was certain that part 2 would break it (and
I was right).

In the end, I chose use this as an exercise in adapting other languages. I took
a Python solution [from
here](https://github.com/mattbillenstein/aoc/blob/main/2024/21/p.py) and
converted it to Clojure. It was an interesting lesson in traversing structures
in a (semi-)functional way, as well as showing me that some of the things I
thought would work didn't. For example, the declaration of `dist` on line 56
needed to be memoized. But being recursive, I couldn't get it to directly
memoize with the `memoize` keyword. So I declare it there as a normal function
and just before I used it, I made a lexical binding for `dist` that was
memoized. But it didn't take, and the calls were all to the non-memo'd one.
Part 2 ran for almost 6 minutes before I accepted that there was something
wrong. So line 69 re-defines the `dist` symbol to be a memoized version of the
function. It works, but it also causes a "redefined-var" warning from the
static analysis tool.

I also need to formalize some of the most-common graph/grid functionality,
preferably in a separte module from `utils.clj`.

## [day22.clj](day22.clj)

Day 22 (19123/17334, unknown).

Continuing from the delays, I didn't even start day 22 until just a few hours
before the 24h mark. Indeed, part 1 was finished at the 23:19:43 mark.

This one was about monkeys. Monkeys and bananas. And bit-wise math operations.
For part 1, you had to apply a PRNG algoritm to each monkey's starting (seed)
number 2000 times, then sum all the results. The operations of the algorithm
were expressed as mutliplication, division (dropping any remainder), and modulo
arithmetic. All of these could be done as bit-wise operations, which were
generally faster.

Part 2 involved determining a 4-number sequence of price differentials that
would yield the best results from each monkey. Only one sequence would be
applied to all monkeys, so a running object/map was used as an ersatz
"memoization" of totals for each diff. It took 7.6s to run, so it can probably
be improved at some point.

## [day23.clj](day23.clj)

Day 23 (4054/4082, 1:53:20).

One of the rare days where I solve both parts back-to-back when the puzzle
unlocks, yet my rank actually went down on part 2. Heh.

The problem was to take a list of pairs of host names, representing network
connections, and determine LANs (sub-groups) with full connectivity. Part 1 was
to find all groups of three in which at least one of the names started with a
"t". This took me 51:07, as I went down the wrong path on graph stuff before
turning to set operations.

Part 2 was a bit more difficult, as you had to find the largest fully-connected
LAN. Here, I spent too much time trying to reuse code from part 1, before
looking at some reddit solutions showed me that (in this case) I actually
needed a graph algorithm. It took just over an hour total, 1:03:13, and I had
both parts done.

## [day24.clj](day24.clj)

Day 24 (4464/8192, 16:57:36).

Started this one on time and finished part 1 in 1:10:36. Upon seeing what part
2 entailed, chose to sleep on it. Looking at solutions on reddit, it seemed a
lot of people solved part 2 by feeding the "circuit" to a graph-generator and
looking for the anomalies. I didn't want to do that, I prefer to think that all
of the puzzles can be solved with code only (aside from the occasional OCR).
After getting up this morning and looking at the conversation on the Clojure
Slack, the answer seemed to come back to the wiring structure of a binary "full
adder".

I started to look at the overall topography of the circuit: There were 312
total "wires", and 222 of them were outputs from logical ops. I also found that
there were 46 "z" wires and 45 each of "x" and "y". Further, none of the x/y
wires were output targets; all were inputs only, with initial values given.
Take these 136 out of the 312, and you 176 wires left (also the result of
subtracting the 46 "z" wires from the 222 outputs). This happens to be `(4 *
44)`, which *should* be the number of logic gates used on a per-pair basis
(give or take for the half-adder used on `x00`/`y00`, etc.).

I worked out some rules based on this, but I got stuck. I ended up looking at
the Python solution
[here](https://github.com/p3rki3/AoC2024/blob/main/Day24_solution.py), as that
person also solved without using a graph-generator. I found out that there were
some corner-cases I hadn't considered and added them.

Then came the moment of truth: none of the sample data for part 1 would produce
a known result for part 2, and the examples given in part 2 were not based on
adder circuits. So, I had an answer with eight components for the puzzle data
but no way of knowing if there were any remaining bugs in my code. I submitted
it and it was the right answer.

Interestingly, part 2 did *not* require actually "running" the simulation as
part 1 had. And it beat the hell out of trying to brute-force all the
combinations of the 222 outputs taken 8 at a time (128,795,283,347,445)
multipled by all permutations within each set of 8 (40,320).

## [day25.clj](day25.clj)

Day 25 (3657/2832, 36:16).

As is usual, only one puzzle on this day. I should have finished it in 25 min
at the most, but something I tried in the REPL somehow got confused (?) when I
copied it to the actual code. As a result, I got the *inverse* set of matches
from what I was supposed to. Third wrong answer of the year. I was using
`some`, I switched to `every?` with a slightly different predicate, and got the
right answer.

