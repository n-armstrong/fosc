/* ------------------------------------------------------------------------------------------------------------
• FoscTimespanList

Timespan list.

a = FoscTimespan(0, 3);
b = FoscTimespan(3, 6);
c = FoscTimespan(6, 10);
t = FoscTimespanList([a, b, c]);
------------------------------------------------------------------------------------------------------------ */
FoscTimespanList : FoscTypedList {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |items|
        ^super.new(items, FoscTimespan);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • allAreContiguous

    Is true when all timespans are contiguous.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    allAreContiguous {
        ^this.notYetImplemented(thisMethod);
        // if len(self) <= 1:
        //     return True
        // timespans = sorted(self[:])
        // last_stop_offset = timespans[0].stop_offset
        // for timespan in timespans[1:]:
        //     if timespan.start_offset != last_stop_offset:
        //         return False
        //     last_stop_offset = timespan.stop_offset
        // return True
    }
    /* --------------------------------------------------------------------------------------------------------
    • allAreNonoverlapping

    Is true when all timespans are nonoverlapping.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    allAreNonoverlapping {
        ^this.notYetImplemented(thisMethod);
        // if len(self) <= 1:
        //     return True
        // timespans = sorted(self[:])
        // last_stop_offset = timespans[0].stop_offset
        // for timespan in timespans[1:]:
        //     if timespan.start_offset < last_stop_offset:
        //         return False
        //     if last_stop_offset < timespan.stop_offset:
        //         last_stop_offset = timespan.stop_offset
        // return True
    }
    /* --------------------------------------------------------------------------------------------------------
    • allAreWellformed

    Is true when all timespans are wellformed.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    allAreWellformed {
        ^this.notYetImplemented(thisMethod);
        // return all(self._get_timespan(argument).wellformed for argument in self)
    }
    /* --------------------------------------------------------------------------------------------------------
    • axis

    Gets axis defined as equal to arithmetic mean of start- and stop-offsets.

    Return offset or nil.
    -------------------------------------------------------------------------------------------------------- */
    axis {
        ^this.notYetImplemented(thisMethod);
        // if self:
        //     return (self.start_offset + self.stop_offset) / 2
    }
    /* --------------------------------------------------------------------------------------------------------
    • duration

    Gets duration of timespan list.

    Returns duration.
    -------------------------------------------------------------------------------------------------------- */
    duration {
        ^this.notYetImplemented(thisMethod);
        // if (self.stop_offset is not Infinity and
        //     self.start_offset is not NegativeInfinity):
        //     return self.stop_offset - self.start_offset
        // else:
        //     return abjad.Duration(0)
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSorted

    Is true when timespans are in time order.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    isSorted {
        ^this.notYetImplemented(thisMethod);
        // if len(self) < 2:
        //     return True
        // pairs = abjad.sequence(self).nwise()
        // for left_timespan, right_timespan in pairs:
        //     if right_timespan.start_offset < left_timespan.start_offset:
        //         return False
        //     if left_timespan.start_offset == right_timespan.start_offset:
        //         if right_timespan.stop_offset < left_timespan.stop_offset:
        //             return False
        // return True
    }
    /* --------------------------------------------------------------------------------------------------------
    • startOffset

    Gets start offset.

    Defined as equal to earliest start offset of any timespan in list.

    Returns offset or nil.

    
    • Example 1

    a = FoscTimespan(0, 3);
    b = FoscTimespan(3, 6);
    c = FoscTimespan(6, 10);
    t = FoscTimespanList([a, b, c]);
    t.startOffset.str;
    -------------------------------------------------------------------------------------------------------- */
    startOffset {
        if (this.items.notEmpty) {
            ^this.items.collect { |each| this.prGetTimespan(each).startOffset }.minItem;
        } {
            ^-inf;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopOffset

    Gets stop offset.

    Defined as equal to latest stop offset of any timespan.

    Returns offset or nil.


    • Example 1

    a = FoscTimespan(0, 3);
    b = FoscTimespan(3, 6);
    c = FoscTimespan(6, 10);
    t = FoscTimespanList([a, b, c]);
    t.stopOffset;
    -------------------------------------------------------------------------------------------------------- */
    stopOffset {
        if (this.items.notEmpty) {
            ^this.items.collect { |each| this.prGetTimespan(each).stopOffset }.maxItem;
        } {
            ^-inf;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • timespan

    Gets timespan of timespan list.

    Returns timespan.
    -------------------------------------------------------------------------------------------------------- */
    timespan {
        ^this.notYetImplemented(thisMethod);
        // return timespans.Timespan(self.start_offset, self.stop_offset)
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • clipTimespanDurations

    Clips timespan durations.
    -------------------------------------------------------------------------------------------------------- */
    clipTimespanDurations { |minimum, maximum, anchor='left'|
        ^this.notYetImplemented(thisMethod);
        // assert anchor in (enums.Left, enums.Right)
        // if minimum is not None:
        //     minimum = abjad.Duration(minimum)
        // if maximum is not None:
        //     maximum = abjad.Duration(maximum)
        // if minimum is not None and maximum is not None:
        //     assert minimum <= maximum
        // timespans = type(self)()
        // for timespan in self:
        //     if minimum is not None and timespan.duration < minimum:
        //         if anchor is enums.Left:
        //             new_timespan = timespan.set_duration(minimum)
        //         else:
        //             new_start_offset = timespan.stop_offset - minimum
        //             new_timespan = abjad.new(
        //                 timespan,
        //                 start_offset=new_start_offset,
        //                 stop_offset=timespan.stop_offset,
        //                 )
        //     elif maximum is not None and maximum < timespan.duration:
        //         if anchor is enums.Left:
        //             new_timespan = timespan.set_duration(maximum)
        //         else:
        //             new_start_offset = timespan.stop_offset - maximum
        //             new_timespan = abjad.new(
        //                 timespan,
        //                 start_offset=new_start_offset,
        //                 stop_offset=timespan.stop_offset,
        //                 )
        //     else:
        //         new_timespan = timespan
        //     timespans.append(new_timespan)
        // return timespans
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeLogicalAnd

    Computes logical AND of timespans.

    Same as setwise intersection.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    computeLogicalAnd {
        ^this.notYetImplemented(thisMethod);
        // if 1 < len(self):
        //     result = self[0]
        //     for timespan in self:
        //         if not timespan.intersects_timespan(result):
        //             self[:] = []
        //             return self
        //         else:
        //             timespans = result & timespan
        //             result = timespans[0]
        //     self[:] = [result]
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeLogicalOr

    Computes logical OR of timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    computeLogicalOr {
        ^this.notYetImplemented(thisMethod);
        // timespans = []
        // if self:
        //     timespans = [self[0]]
        //     for timespan in self[1:]:
        //         if timespans[-1]._can_fuse(timespan):
        //             timespans_ = timespans[-1] | timespan
        //             timespans[-1:] = timespans_[:]
        //         else:
        //             timespans.append(timespan)
        // self[:] = timespans
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeLogicalXor

    Computes logical XOR of timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    computeLogicalXor {
        ^this.notYetImplemented(thisMethod);
        // all_fragments = []
        // for i, timespan_1 in enumerate(self):
        //     timespan_1_fragments = [timespan_1]
        //     for j, timespan_2 in enumerate(self):
        //         if i == j:
        //             continue
        //         revised_timespan_1_fragments = []
        //         for timespan_1_fragment in timespan_1_fragments:
        //             if timespan_2.intersects_timespan(timespan_1_fragment):
        //                 result = timespan_1_fragment - timespan_2
        //                 revised_timespan_1_fragments.extend(result)
        //             else:
        //                 revised_timespan_1_fragments.append(
        //                     timespan_1_fragment)
        //         timespan_1_fragments = revised_timespan_1_fragments
        //     all_fragments.extend(timespan_1_fragments)
        // self[:] = all_fragments
        // self.sort()
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeOverlapFactor

    Computes overlap factor of timespans.

    Returns multiplier.
    -------------------------------------------------------------------------------------------------------- */
    computeOverlapFactor { |timespan|
        // if timespan is None:
        //     timespan = self.timespan
        // time_relation = abjad.timespans.timespan_2_intersects_timespan_1(
        //     timespan_1=timespan)
        // timespans = self.get_timespans_that_satisfy_time_relation(
        //     time_relation)
        // total_overlap = abjad.Duration(sum(
        //     x.get_overlap_with_timespan(timespan) for x in timespans))
        // overlap_factor = total_overlap / timespan.duration
        // return overlap_factor
        var timeRelation, timespans, totalOverlap, overlapFactor;
        if (timespan.isNil) { timespan = this.timespan };
        timeRelation = FoscTimespan.timespan2IntersectsTimespan1(timespan1: timespan);
        timespans = this.getTimespansThatSatisfyTimeRelation(timeRelation);
        totalOverlap = FoscDuration(timespans.collect { |each| each.getOverlapWithTimespan(timespan) });
        overlapFactor = totalOverlap / timespan.duration;
        ^overlapFactor;
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeOverlapFactorMapping

    Computes overlap factor for each consecutive offset pair in timespans.

    Returns mapping.
    -------------------------------------------------------------------------------------------------------- */
    computeOverlapFactorMapping {
        // mapping = collections.OrderedDict()
        // offsets = abjad.sequence(sorted(self.count_offsets()))
        // for start_offset, stop_offset in offsets.nwise():
        //     timespan = abjad.Timespan(start_offset, stop_offset)
        //     overlap_factor = self.compute_overlap_factor(timespan=timespan)
        //     mapping[timespan] = overlap_factor
        // return mapping
        var mapping, offsets, timespan, overlapFactor;
        mapping = ();
        offsets = this.countOffsets.sort;
        offsets.doAdjacentPairs { |startOffset, stopOffset|
            timespan = FoscTimespan(startOffset, stopOffset);
            overlapFactor = this.computerOverlapFactor(timespan: timespan);
            mapping[timespan] = overlapFactor;
        };
        ^mapping; //!!!TODO: nb. not a sorted dictionary
    }
    /* --------------------------------------------------------------------------------------------------------
    • countOffsets

    Counts offsets.

    Returns counter.
    -------------------------------------------------------------------------------------------------------- */
    countOffsets {
        ^this.notYetImplemented(thisMethod);
        // return meter.OffsetCounter(self)
        // ^FoscOffsetCounter(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • explode

    Explodes timespans into timespan lists, avoiding overlap, and distributing density as evenly as possible.

    Returns timespan lists.
    -------------------------------------------------------------------------------------------------------- */
    explode { |inventoryCount|
        var boundingTimespan, globalOverlapFactors, emptyTimespanPairs, resultTimespanLists, resultTimespans;
        var currentOverlapFactor, nonOverlappingTimespanLists, overlappingTimespanLists, localOverlapFactor;
        var emptyTimespans, globalOverlapFactor, i;
        // assert isinstance(inventory_count, (type(None), int))
        // if isinstance(inventory_count, int):
        //     assert 0 < inventory_count
        // bounding_timespan = self.timespan
        // global_overlap_factors = []
        // empty_timespans_pairs = []
        // result_timespan_lists = []
        assert([Integer, Nil].any { |type| inventoryCount.isKindOf(type) });
        if (inventoryCount.isInteger) { assert(inventoryCount > 0) };
        boundingTimespan = this.timespan;
        globalOverlapFactors = [];
        emptyTimespanPairs = [];
        resultTimespanLists = [];
        // if inventory_count is not None:
        //     for i in range(inventory_count):
        //         global_overlap_factors.append(0)
        //         result_timespans = type(self)([])
        //         empty_timespans_pairs.append((i, result_timespans))
        //         result_timespan_lists.append(result_timespans)
        if (inventoryCount.notNil) {
            inventoryCount.do { |i|
                globalOverlapFactors = globalOverlapFactors.add(0);
                resultTimespans = this.species.new([]);
                emptyTimespanPairs = emptyTimespanPairs.add([i, resultTimespans]);
                resultTimespanLists = resultTimespanLists.add(resultTimespans);
            };
        };
        // for current_timespan in self:
        //     current_overlap_factor = \
        //         current_timespan.duration / bounding_timespan.duration
        this.do { |currentTimespan|
            currentOverlapFactor = currentTimespan.duration / boundingTimespan.duration;
        //     if empty_timespans_pairs:
        //         i, empty_timespans = empty_timespans_pairs.pop()
        //         empty_timespans.append(current_timespan)
        //         global_overlap_factors[i] = current_overlap_factor
        //         continue
            case
            { emptyTimespanPairs.notEmpty } {
                # i, emptyTimespans = emptyTimespanPairs.pop;
                emptyTimespans = emptyTimespans.add(currentTimespan);
                globalOverlapFactors[i] = currentOverlapFactor;
            } {
        //     nonoverlapping_timespan_lists = []
        //     overlapping_timespan_lists = []
                nonOverlappingTimespanLists = [];
                overlappingTimespanLists = [];  
        //     for i, result_timespans in enumerate(result_timespan_lists):
        //         local_overlap_factor = result_timespans.compute_overlap_factor(
        //             current_timespan)
        //         global_overlap_factor = global_overlap_factors[i]
        //         if not local_overlap_factor:
        //             nonoverlapping_timespan_lists.append(
        //                 (i, global_overlap_factor))
        //         else:
        //             overlapping_timespan_lists.append(
        //                 (i, local_overlap_factor, global_overlap_factor))
                resultTimespanLists.do { |resultTimespans, i|
                    localOverlapFactor = resultTimespans.computeOverlapFactor(currentTimespan);
                    globalOverlapFactor = globalOverlapFactors[i];
                    //••••!!! can 'computeOverlapFactor' return nil
                    if (localOverlapFactor.isNil) {
                        nonOverlappingTimespanLists = 
                            nonOverlappingTimespanLists.add([i, globalOverlapFactor]);
                    } {
                        overlappingTimespanLists =
                            overlappingTimespanLists.add([i, localOverlapFactor, globalOverlapFactor]);
                    };
                };
        //     nonoverlapping_timespan_lists.sort(key=lambda x: x[1])
        //     overlapping_timespan_lists.sort(key=lambda x: (x[1], x[2]))
                nonOverlappingTimespanLists = nonOverlappingTimespanLists.sort { |a, b| a[1] < b[1] };
                //••••!!! must sort next 2D
                overlappingTimespanLists = overlappingTimespanLists.sort { |a, b| a[1] < b[1] };
        //     if not nonoverlapping_timespan_lists and inventory_count is None:
        //         result_timespans = type(self)([current_timespan])
        //         global_overlap_factors.append(current_overlap_factor)
        //         result_timespan_lists.append(result_timespans)
        //         continue
                if (nonOverlappingTimespanLists.isEmpty && { inventoryCount.isNil }) {
                    resultTimespans = this.species.new([currentTimespan]);
                    globalOverlapFactors = globalOverlapFactors.add(currentOverlapFactor);
                    resultTimespanLists = resultTimespanLists.add(resultTimespans);
                } {
            //     if nonoverlapping_timespan_lists:
            //         i = nonoverlapping_timespan_lists[0][0]
            //     else:
            //         i = overlapping_timespan_lists[0][0]
            
            //     result_timespans = result_timespan_lists[i]
            //     result_timespans.append(current_timespan)
            //     global_overlap_factors[i] += current_overlap_factor
                    if (nonOverlappingTimespanLists.notEmpty) {
                        i = nonOverlappingTimespanLists[0][0];
                    } {
                        i = overlappingTimespanLists[0][0];
                    };
                    resultTimespans = resultTimespanLists[i];
                    resultTimespans = resultTimespans.add(currentTimespan);
                    globalOverlapFactors[i] = globalOverlapFactors[i] + currentOverlapFactor;
                };
            };
        };
        // return tuple(result_timespan_lists)
        ^resultTimespanLists;
    }
    /* --------------------------------------------------------------------------------------------------------
    • getTimespanThatSatisfiesTimeRelation

    Gets timespan that satisifies 'time_relation'.

    Returns timespan when timespan list contains exactly one timespan that satisfies 'time_relation'.

    Raises exception when timespan list contains no timespan that satisfies 'time_relation'.

    Raises exception when timespan list contains more than one timespan that satisfies 'time_relation'.
    -------------------------------------------------------------------------------------------------------- */
    getTimespanThatSatisfiesTimeRelation { |timeRelation|
        ^this.notYetImplemented(thisMethod);
        // timespans = self.get_timespans_that_satisfy_time_relation(
        //     time_relation)
        // if len(timespans) == 1:
        //     return timespans[0]
        // elif 1 < len(timespans):
        //     message = 'extra timespan.'
        //     raise Exception(message)
        // else:
        //     message = 'missing timespan.'
        //     raise Exception(message)
    }
    /* --------------------------------------------------------------------------------------------------------
    • getTimespansThatSatisfyTimeRelation

    Gets timespans that satisfy 'time_relation'.

    Returns new timespan list.
    -------------------------------------------------------------------------------------------------------- */
    getTimespansThatSatisfyTimeRelation { |timeRelation|
        ^this.notYetImplemented(thisMethod);
        // result = []
        // for timespan in self:
        //     if isinstance(
        //         time_relation,
        //         timespans.TimespanTimespanTimeRelation):
        //         if time_relation(timespan_2=timespan):
        //             result.append(timespan)
        //     elif isinstance(
        //         time_relation,
        //         timespans.OffsetTimespanTimeRelation):
        //         if time_relation(timespan=timespan):
        //             result.append(timespan)
        //     else:
        //         message = 'unknown time relation: {!r}.'
        //         message = message.format(time_relation)
        //         raise ValueError(message)
        // return type(self)(result)
    }
    /* --------------------------------------------------------------------------------------------------------
    • hasTimespanThatSatisfiesTimeRelation

    Is true when timespan list has timespan that satisfies 'time_relation'.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    hasTimespanThatSatisfiesTimeRelation { |timeRelation|
        ^this.notYetImplemented(thisMethod);
        // return bool(
        //     self.get_timespans_that_satisfy_time_relation(time_relation))
    }
    /* --------------------------------------------------------------------------------------------------------
    • partition

    Partitions timespans into timespan_lists.

    Returns zero or more timespan_lists.
    -------------------------------------------------------------------------------------------------------- */
    partition { |includeTangentTimespans=false|
        ^this.notYetImplemented(thisMethod);
        // if not self:
        //     return []
        // timespan_lists = []
        // timespans = sorted(self[:])
        // current_list = type(self)([timespans[0]])
        // latest_stop_offset = current_list[0].stop_offset
        // for current_timespan in timespans[1:]:
        //     if current_timespan.start_offset < latest_stop_offset:
        //         current_list.append(current_timespan)
        //     elif (include_tangent_timespans and
        //         current_timespan.start_offset == latest_stop_offset):
        //         current_list.append(current_timespan)
        //     else:
        //         timespan_lists.append(current_list)
        //         current_list = type(self)([current_timespan])
        //     if latest_stop_offset < current_timespan.stop_offset:
        //         latest_stop_offset = current_timespan.stop_offset
        // if current_list:
        //     timespan_lists.append(current_list)
        // return tuple(timespan_lists)
    }
    /* --------------------------------------------------------------------------------------------------------
    • reflect

    Reflects timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    reflect { |axis|    
        ^this.notYetImplemented(thisMethod);
        // if axis is None:
        //     axis = self.axis
        // timespans = []
        // for timespan in self:
        //     timespan = timespan.reflect(axis=axis)
        //     timespans.append(timespan)
        // timespans.reverse()
        // self[:] = timespans
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • removeDegenerateTimespans

    Removes degenerate timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    removeDegenerateTimespans {
        ^this.notYetImplemented(thisMethod);
        // timespans = [x for x in self if x.wellformed]
        // self[:] = timespans
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • repeatToStopOffset

    Repeats timespans to 'stop_offset'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    repeatToStopOffset { |stopOffset|
        ^this.notYetImplemented(thisMethod);
        // assert self.is_sorted
        // stop_offset = abjad.Offset(stop_offset)
        // assert self.stop_offset <= stop_offset
        // current_timespan_index = 0
        // if self:
        //     while self.stop_offset < stop_offset:
        //         current_timespan = self[current_timespan_index]
        //         translation = self.stop_offset - current_timespan.start_offset
        //         new_timespan = current_timespan.translate(translation)
        //         self.append(new_timespan)
        //         current_timespan_index += 1
        //     if stop_offset < self.stop_offset:
        //         self[-1] = self[-1].set_offsets(stop_offset=stop_offset)
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • rotate

    Rotates by 'count' contiguous timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    rotate { |count|
        ^this.notYetImplemented(thisMethod);
        // assert isinstance(count, int)
        // assert self.all_are_contiguous
        // elements_to_move = count % len(self)
        // if elements_to_move == 0:
        //     return
        // left_timespans = self[:-elements_to_move]
        // right_timespans = self[-elements_to_move:]
        // split_offset = right_timespans[0].start_offset
        // translation_to_left = split_offset - self.start_offset
        // translation_to_left *= -1
        // translation_to_right = self.stop_offset - split_offset
        // translated_right_timespans = []
        // for right_timespan in right_timespans:
        //     translated_right_timespan = right_timespan.translate_offsets(
        //         translation_to_left, translation_to_left)
        //     translated_right_timespans.append(translated_right_timespan)
        // translated_left_timespans = []
        // for left_timespan in left_timespans:
        //     translated_left_timespan = left_timespan.translate_offsets(
        //         translation_to_right, translation_to_right)
        //     translated_left_timespans.append(translated_left_timespan)
        // new_timespans = translated_right_timespans + translated_left_timespans
        // self[:] = new_timespans
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • roundOffsets

    Rounds offsets of timespans in list to multiples of 'multiplier'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    roundOffsets { |multiplier, anchor='left', mustBeWellformed=true|
        ^this.notYetImplemented(thisMethod);
        // timespans = []
        // for timespan in self:
        //     timespan = timespan.round_offsets(
        //         multiplier,
        //         anchor=anchor,
        //         must_be_wellformed=must_be_wellformed,
        //         )
        //     timespans.append(timespan)
        // self[:] = timespans
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • scale

    Scales timespan by 'multiplier' relative to 'anchor'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    scale { |multiplier, anchor='left'|
        ^this.notYetImplemented(thisMethod);
        // timespans = []
        // for timespan in self:
        //     timespan = timespan.scale(multiplier, anchor=anchor)
        //     timespans.append(timespan)
        // self[:] = timespans
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • splitAtOffset

    Splits timespans at 'offset'.

    Returns timespan_lists.
    -------------------------------------------------------------------------------------------------------- */
    splitAtOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // before_list = type(self)()
        // during_list = type(self)()
        // after_list = type(self)()
        // for timespan in self:
        //     if timespan.stop_offset <= offset:
        //         before_list.append(timespan)
        //     elif offset <= timespan.start_offset:
        //         after_list.append(timespan)
        //     else:
        //         during_list.append(timespan)
        // for timespan in during_list:
        //     before_timespan, after_timespan = timespan.split_at_offset(offset)
        //     before_list.append(before_timespan)
        //     after_list.append(after_timespan)
        // before_list.sort()
        // after_list.sort()
        // return before_list, after_list
    }
    /* --------------------------------------------------------------------------------------------------------
    • splitAtOffsets

    Splits timespans at 'offsets'.

    Returns one or more timespan_lists.
    -------------------------------------------------------------------------------------------------------- */
    splitAtOffsets { |offsets|
        ^this.notYetImplemented(thisMethod);
        // timespan_lists = [self]
        // if not self:
        //     return timespan_lists
        // offsets = sorted(set(abjad.Offset(x) for x in offsets))
        // offsets = [x for x in offsets
        //     if self.start_offset < x < self.stop_offset]
        // for offset in offsets:
        //     shards = [x for x in timespan_lists[-1].split_at_offset(offset)
        //         if x]
        //     if shards:
        //         timespan_lists[-1:] = shards
        // return timespan_lists
    }
    /* --------------------------------------------------------------------------------------------------------
    • stretch

    Stretches timespans by 'multiplier' relative to 'anchor'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    stretch { |multiplier, anchor|
        ^this.notYetImplemented(thisMethod);
        // timespans = []
        // if anchor is None:
        //     anchor = self.start_offset
        // for timespan in self:
        //     timespan = timespan.stretch(multiplier, anchor)
        //     timespans.append(timespan)
        // self[:] = timespans
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • translate

    Translates timespans by 'translation'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    translate { |translation|
        ^this.notYetImplemented(thisMethod);
        // return self.translate_offsets(translation, translation)
    }
    /* --------------------------------------------------------------------------------------------------------
    • translateOffsets

    Translates timespans by 'start_offset_translation' and \'stop_offset_translation'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    translateOffsets { |startOffsetTranslation, stopOffsetTranslation|
        ^this.notYetImplemented(thisMethod);
        // timespans = []
        // for timespan in self:
        //     timespan = timespan.translate_offsets(
        //         start_offset_translation,
        //         stop_offset_translation,
        //         )
        //     timespans.append(timespan)
        // self[:] = timespans
        // return self
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • difference (abjad: __sub__)

    Deletes material that intersects 'timespan'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    difference { |timespan|
        ^this.notYetImplemented(thisMethod);
        // new_timespans = []
        // for current_timespan in self[:]:
        //     result = current_timespan - timespan
        //     new_timespans.extend(result)
        // self[:] = sorted(new_timespans)
        // return self
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    Illustrates timespans.

    Returns LilyPond file.


    • Example 1

    a = FoscTimespan(0, 3);
    b = FoscTimespan(3, 6);
    c = FoscTimespan(6, 10);
    t = FoscTimespanList([a, b, c]);
    t.illustrate;
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |key, range, sortkey, scale=1|
        var minimum, maximum, postscriptScale, postscriptXOffset, timespanLists, markup, val, markups;
        var timespans, valueMarkup, vspaceMarkup, timespanMarkup;
        // if not self:
        //     return abjad.Markup.null().__illustrate__()
        // if isinstance(range_, abjad.Timespan):
        //     minimum, maximum = range_.start_offset, range_.stop_offset
        // elif range_ is not None:
        //     minimum, maximum = range_
        // else:
        //     minimum, maximum = self.start_offset, self.stop_offset
        // if scale is None:
        //     scale = 1.
        if (this.items.isEmpty) { ^FoscMarkup().null.illustrate };
        \A.postln;
        case
        { range.isKindOf(FoscTimespan) } {
            # minimum, maximum = [range.startOffset, range.stopOffset];
        }
        { range.notNil } {
            # minimum, maximum = range;
        }
        {
            # minimum, maximum = [this.startOffset, this.stopOffset];
        };  

        // assert 0 < scale
        // minimum = float(abjad.Offset(minimum))
        // maximum = float(abjad.Offset(maximum))
        // postscript_scale = 150. / (maximum - minimum)
        // postscript_scale *= float(scale)
        // postscript_x_offset = (minimum * postscript_scale) - 1
        \B.postln;
        minimum = FoscOffset(minimum).asFloat;
        maximum = FoscOffset(maximum).asFloat;
        postscriptScale = 150 / (maximum - minimum);
        postscriptScale = postscriptScale * scale;
        postscriptXOffset = (minimum * postscriptScale) - 1;
        
        // if key is None:
        //     markup = self._make_timespan_list_markup(
        //         self,
        //         postscript_x_offset,
        //         postscript_scale,
        //         sortkey=sortkey,
        //         )
        \C.postln;
        if (key.isNil) {
            \C1.postln;
            markup = FoscTimespanList.prMakeTimespanListMarkup(
                this, postscriptXOffset, postscriptScale, sortkey: sortkey);
        } {
        // else:
        //     timespan_lists = {}
        //     for timespan in self:
        //         value = getattr(timespan, key)
        //         if value not in timespan_lists:
        //             timespan_lists[value] = type(self)()
        //         timespan_lists[value].append(timespan)
        //     markups = []
            \C2.postln;
            timespanLists = ();
            this.do { |timespan|
                val = if (timespan.respondsTo(key)) { timespan.perform(key) };
                if (timespanLists[val].isNil) {
                    timespanLists[val] = this.species.new;
                };
                timespanLists[val].add(timespan);
            };
            markups = [];

        //     for i, item in enumerate(sorted(timespan_lists.items())):
        //         value, timespans = item
        //         timespans.sort()
        //         if 0 < i:
        //             vspace_markup = abjad.Markup.vspace(0.5)
        //             markups.append(vspace_markup)
        //         value_markup = abjad.Markup('{}:'.format(value))
        //         value_markup = abjad.Markup.line([value_markup])
        //         value_markup = value_markup.sans().fontsize(-1)
        //         markups.append(value_markup)
        //         vspace_markup = abjad.Markup.vspace(0.5)
        //         markups.append(vspace_markup)
        //         timespan_markup = self._make_timespan_list_markup(
        //             timespans,
        //             postscript_x_offset,
        //             postscript_scale,
        //             sortkey=sortkey,
        //             )
        //         markups.append(timespan_markup)
        //     markup = abjad.Markup.left_column(markups)
            \C3.postln;
            timespanLists.asSortedArray.do { |pair, i|
                # val, timespans = pair;
                timespans = timespans.sort;
                if (0 < i) {
                    vspaceMarkup = FoscMarkup.vspace(0.5);
                    markups = markups.add(vspaceMarkup);
                };
                valueMarkup = FoscMarkup("%:".format(val));
                valueMarkup = FoscMarkup.line([valueMarkup]);
                valueMarkup = valueMarkup.sans.fontsize(-1);
                markups = markups.add(valueMarkup);
                vspaceMarkup = FoscMarkup.vspace(0.5);
                markups = markups.add(vspaceMarkup);
                timespanMarkup = FoscTimespanList.prMakeTimespanListMarkup(
                    timespans, postscriptXOffset, postscriptScale, sortkey: sortkey);
                markups = markups.add(timespanMarkup);
            };
            markup = FoscMarkup.leftColumn(markups);
        };
        \D.postln;
        // return markup.__illustrate__()
        ^markup.illustrate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • invert

    a = (a: 1, b: 2);
    a.asSortedArray

    Inverts timespans.

    Returns new timespan list.
    -------------------------------------------------------------------------------------------------------- */
    invert {
        ^this.notYetImplemented(thisMethod);
        // result = type(self)()
        // result.append(self.timespan)
        // for timespan in self:
        //     result = result - timespan
        // return result
    }
    /* --------------------------------------------------------------------------------------------------------
    • union (abjad: __and__)

    Keeps material that intersects 'timespan'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    union { |timespan|
        ^this.notYetImplemented(thisMethod);
        // new_timespans = []
        // for current_timespan in self[:]:
        //     result = current_timespan & timespan
        //     new_timespans.extend(result)
        // self[:] = sorted(new_timespans)
        // return self
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prItemCoercer
    -------------------------------------------------------------------------------------------------------- */
    prItemCoercer { |object|
        case
        { FoscTimespan.prImplementsTimespanInterface(object) } { ^object }
        { object.isKindOf(FoscTimespan) } { ^object }
        { object.isSequenceableCollection && { object.size == 2 } } { ^FoscTimespan(*object) }
        { ^FoscTimespan(object) };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prMakeTimespanListMarkup

    a = FoscTimespan(0, 3);
    b = FoscTimespan(3, 6);
    c = FoscTimespan(6, 10);
    t = FoscTimespanList([a, b, c]);
    t.illustrate;
    -------------------------------------------------------------------------------------------------------- */
    *prMakeTimespanListMarkup { |timespans, postscriptXOffset, postscriptScale, drawOffsets=true, sortkey|
        var explodedTimespanLists, sortedTimespanLists, key, val, ps, offsetMapping, height, postscriptYOffset;
        var markup, level, xOffset, xExtent, yExtent, linesMarkup, fractionMarkups, offset, numerator;
        var denominator, fraction, xTranslation, fractionMarkup;
        
        // exploded_timespan_lists = []
        // if not sortkey:
        //     exploded_timespan_lists.extend(timespans.explode())
        // else:
        //     sorted_timespan_lists = {}
        //     for timespan in timespans:
        //         value = getattr(timespan, sortkey)
        //         if value not in sorted_timespan_lists:
        //             sorted_timespan_lists[value] = TimespanList()
        //         sorted_timespan_lists[value].append(timespan)
        //     for key, timespans in sorted(sorted_timespan_lists.items()):
        //         exploded_timespan_lists.extend(timespans.explode())
        explodedTimespanLists = [];
        if (sortkey.isNil) {
            \a1.postln;
            explodedTimespanLists = explodedTimespanLists.addAll(timespans.explode); 
        } {
            \a2.postln;
            sortedTimespanLists = ();
            timespans.do { |timespan|
                val = if (timespan.respondsTo(sortkey)) { timespan.perform(sortkey) };
                if (sortedTimespanLists[val].isNil) {
                    sortedTimespanLists[val] = FoscTimespanList();
                };
                sortedTimespanLists[val].add(timespan);
            };
            sortedTimespanLists.asSortedArray.do { |pair|
                # key, timespans = pair;
                explodedTimespanLists = explodedTimespanLists.addAll(timespans.explode);
            };
        };
        // ps = abjad.Postscript()
        // ps = ps.setlinewidth(0.2)
        // offset_mapping = {}
        // height = ((len(exploded_timespan_lists) - 1) * 3) + 1
        \b.postln;
        ps = FoscPostscript();
        ps = ps.setlinewidth(0.2);  
        offsetMapping = ();
        height = ((explodedTimespanLists.size - 1) * 3) + 1;
        // for level, timespans in enumerate(exploded_timespan_lists, 0):
        //     postscript_y_offset = height - (level * 3) - 0.5
        //     for timespan in timespans:
        //         offset_mapping[timespan.start_offset] = level
        //         offset_mapping[timespan.stop_offset] = level
        //         ps += timespan._as_postscript(
        //             postscript_x_offset,
        //             postscript_y_offset,
        //             postscript_scale,
        //             )
        \c.postln;
        explodedTimespanLists.do { |timespans, level|
            postscriptYOffset = height - (level * 3) - 0.5;
            timespans.do { |timespan|
                offsetMapping(timespan.startOffset) = level;
                offsetMapping(timespan.stopOffset) = level;
                ps = ps ++ timespan.prAsPostscript(postscriptXOffset, postscriptYOffset, postscriptScale);
            };
        };
        // if not draw_offsets:
        //     markup = abjad.Markup.postscript(ps)
        //     return markup
        // ps = ps.setlinewidth(0.1)
        // ps = ps.setdash([0.1, 0.2])
        \d.postln;
        if (drawOffsets.not) {
            markup = FoscMarkup.postscript(ps);
            ^markup;
        }; 
        // for offset in sorted(offset_mapping):
        //     level = offset_mapping[offset]
        //     x_offset = (float(offset) * postscript_scale)
        //     x_offset -= postscript_x_offset
        //     ps = ps.moveto(x_offset, height + 1.5)
        //     ps = ps.lineto(x_offset, height - (level * 3))
        //     ps = ps.stroke()
        \e.postln;
        offsetMapping.asSortedArray.do { |pair|
            # offset, val = pair;
            level = offsetMapping(offset);
            xOffset = offset.asFloat * postscriptScale;
            xOffset = xOffset - postscriptXOffset;
            ps = ps.moveto(xOffset, height + 1.5);
            ps = ps.lineto(xOffset, height - (level * 3));
            ps = ps.stroke;
        };
        // ps = ps.moveto(0, 0)
        // ps = ps.setgray(0.99)
        // ps = ps.rlineto(0, 0.01)
        // ps = ps.stroke()
        \f.postln;
        ps = ps.moveto(0, 0);
        ps = ps.setgray(0.99);
        ps = ps.rlineto(0, 0.01);
        ps = ps.stroke;
        // x_extent = float(timespans.stop_offset)
        // x_extent *= postscript_scale
        // x_extent += postscript_x_offset
        // x_extent = (0, x_extent)
        // y_extent = (0, height + 1.5)
        // lines_markup = abjad.Markup.postscript(ps)
        // lines_markup = lines_markup.pad_to_box(x_extent, y_extent)
        // fraction_markups = []
        \g.postln;
        xExtent = timespans.stopOffset.asFloat;
        xExtent = xExtent * postscriptScale;
        xExtent = xExtent + postscriptXOffset;
        xExtent = [0, xExtent];
        yExtent = [0, height + 1.5];
        linesMarkup = FoscMarkup.postscript(ps);
        linesMarkup = linesMarkup.padToBox(xExtent, yExtent);
        fractionMarkups = [];
        // for offset in sorted(offset_mapping):
        //     offset = abjad.Multiplier(offset)
        //     numerator, denominator = offset.numerator, offset.denominator
        //     fraction = abjad.Markup.fraction(numerator, denominator)
        //     fraction = fraction.center_align().fontsize(-3).sans()
        //     x_translation = (float(offset) * postscript_scale)
        //     x_translation -= postscript_x_offset
        //     fraction = fraction.translate((x_translation, 1))
        //     fraction_markups.append(fraction)
        \h.postln;
        offsetMapping.asSortedArray.do { |pair|
            # offset, val = pair;
            offset = FoscMultiplier(offset);
            # numerator, denominator = [offset.numerator, offset, denominator];
            fraction = FoscMarkup.fraction(numerator, denominator);
            fraction = fraction.centerAlign.fontsize(-3).sans;
            xTranslation = offset.asFloat * postscriptScale;
            xTranslation = xTranslation - postscriptXOffset;
            fraction = fraction.translate([xTranslation, 1]);
            fractionMarkups = fractionMarkups.add(fraction);
        };
        // fraction_markup = abjad.Markup.overlay(fraction_markups)
        // markup = abjad.Markup.column([fraction_markup, lines_markup])
        // return markup
        \i.postln;
        fractionMarkup = FoscMarkup.overlay(fractionMarkups);
        markup = FoscMarkup.column([fractionMarkup, linesMarkup]);
        \j.postln;
        ^markup;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetOffsets
    -------------------------------------------------------------------------------------------------------- */
    prGetOffsets { |object|
        try { ^[object.startOffset, object.stopOffset] };  
        try { ^object.timespan.offsets };
        throw("%:%: can't get offsets for object: %.".format(this.species, thisMethod.name, object));
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetTimespan
    -------------------------------------------------------------------------------------------------------- */
    prGetTimespan { |argument|
        var startOffset, stopOffset;
        # startOffset, stopOffset = this.prGetOffsets(argument);
        ^FoscTimespan(startOffset, stopOffset);
    }
}
