/* ------------------------------------------------------------------------------------------------------------
• FoscTimespan

Timespan.

Timespans are closed-open intervals.

Timespans are immutable.

a = FoscTimespan(0, 10);
b = FoscTimespan(5, 12);
c = FoscTimespan(-2, 2);
d = FoscTimespan(10, 20);
------------------------------------------------------------------------------------------------------------ */
FoscTimespan : FoscObject {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <startOffset, <stopOffset;
    var <publishStorageFormat=true;
    *new { |startOffset= -inf, stopOffset=inf|
        ^super.new.init(startOffset, stopOffset);
    }
    init { |argStartOffset, argStopOffset|
        startOffset = this.prInitializeOffset(argStartOffset);
        stopOffset = this.prInitializeOffset(argStopOffset);
        if (stopOffset < startOffset) {
            throw("%::new: stopOffset % is less than startOffset %."
                .format(this.species, stopOffset.str, startOffset.str));
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • axis

    Arithmetic mean of 'startOffset' and 'stopOffset'.


    • Example 1

    FoscTimespan(0, 10).axis.str;
    -------------------------------------------------------------------------------------------------------- */
    axis {
        ^((startOffset + stopOffset) / 2);
    }
    /* --------------------------------------------------------------------------------------------------------
    • duration

    Duration of timespan.


    • Example 1

    FoscTimespan(0, 10).duration.str;
    -------------------------------------------------------------------------------------------------------- */
    duration {
        ^(stopOffset - startOffset);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isWellformed

    Is true when timespan start offset preceeds timespan stop offset.


     • Example 1

    FoscTimespan(0, 10).isWellformed;
    -------------------------------------------------------------------------------------------------------- */
    isWellformed {
        ^(startOffset < stopOffset);
    }
    /* --------------------------------------------------------------------------------------------------------
    • offsets

    Timespan offsets.


    • Example 1

    FoscTimespan(0, 10).offsets.collect { |each| each.str };
    -------------------------------------------------------------------------------------------------------- */
    offsets {
        ^[startOffset, stopOffset];
    }
    /* --------------------------------------------------------------------------------------------------------
    • startOffset

    Timespan start offset.


    • Example 1

    FoscTimespan(0, 10).startOffset.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • stopOffset

    Timespan stop offset.


    • Example 1

    FoscTimespan(0, 10).stopOffset.str;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • && (abjad: __and__)
    
    Logical AND of two timespans.
    -------------------------------------------------------------------------------------------------------- */
    && {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • == (abjad: __eq__)
    
    Is true when 'timespan' is a timespan with equal offsets. Otherwise false:
    
    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    == {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • format
    
    Formats timespan.
    
    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • >= (abjad: __ge__)
    
    Is true when 'argument' start offset is greater or equal to timespan start offset. Otherwise false:
    
    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    >= {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • > (abjad: __gt__)
    
    Is true when 'argument' start offset is greater than timespan start offset. Otherwise false:
    
    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    > {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash
    
    Hashes timespan.
    -------------------------------------------------------------------------------------------------------- */
    hash {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    Illustrates timespan.
    
    Returns Lilypond file.
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |range, scale|
        var timespans;
        timespans = FoscTimespanList([this]);
        ^timespans.illustrate(range: range, scale: scale);
    }
    /* --------------------------------------------------------------------------------------------------------
    • <= (abjad: __le__)
    
    Is true when 'argument' start offset is less than or equal to timespan start offset. Otherwise false:

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    <= {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • size
    
   Defined equal to 1 for all timespans.
    
    Returns positive integer.
    -------------------------------------------------------------------------------------------------------- */
    size {
        ^1;
    }
    /* --------------------------------------------------------------------------------------------------------
    • < (abjad: __lt__ )
    
    Is true when 'argument' start offset is less than timespan start offset. Otherwise false:
    
    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    < {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • || (abjad: __or__)
    
    Logical OR of two timespans.
    
    Returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    || {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • - (abjad: __sub__)
    
    Subtract 'argument' from timespan.
    
    Returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    - {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • xor (abjad: __xor__)
    
    Logical XOR of two timespans.
    
    Returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    xor {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAsPostscript
    -------------------------------------------------------------------------------------------------------- */
    prAsPostscript { |postscriptXOffset, postscriptYOffset, postscriptScale|
        var start, stop, ps;
        // start = (float(self._start_offset) * postscript_scale)
        // start -= postscript_x_offset
        // stop = (float(self._stop_offset) * postscript_scale)
        // stop -= postscript_x_offset
        start = startOffset.asFloat * postscriptScale;
        start = start - postscriptYOffset;
        stop = stopOffset.asFloat * postscriptScale;
        stop = stop - postscriptXOffset;  
        // ps = abjad.Postscript()
        // ps = ps.moveto(start, postscript_y_offset)
        // ps = ps.lineto(stop, postscript_y_offset)
        // ps = ps.stroke()
        ps = FoscPostscript();
        ps = ps.moveto(start, postscriptYOffset);
        ps = ps.lineto(stop, postscriptYOffset);
        ps = ps.stroke;
        // ps = ps.moveto(start, postscript_y_offset + 0.75)
        // ps = ps.lineto(start, postscript_y_offset - 0.75)
        // ps = ps.stroke()
        ps = ps.moveto(start, postscriptYOffset + 0.75);
        ps = ps.moveto(start, postscriptYOffset - 0.75);
        ps = ps.stroke;
        // ps = ps.moveto(stop, postscript_y_offset + 0.75)
        // ps = ps.lineto(stop, postscript_y_offset - 0.75)
        // ps = ps.stroke()
        // return ps
        ps = ps.moveto(start, postscriptYOffset + 0.75);
        ps = ps.moveto(start, postscriptYOffset - 0.75);
        ps = ps.stroke;
        ^ps;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prCanFuse
    -------------------------------------------------------------------------------------------------------- */
    prCanFuse { |argument|
        ^this.notYetImplemented(thisMethod);
        // if isinstance(argument, type(self)):
        //     return self.intersects_timespan(argument) or \
        //         self.stops_when_timespan_starts(argument)
        // return False
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetTimespan
    -------------------------------------------------------------------------------------------------------- */
    prGetTimespan { |argument|
        ^this.notYetImplemented(thisMethod);
        // if isinstance(argument, Timespan):
        //     start_offset, stop_offset = argument.offsets
        // elif hasattr(argument, 'timespan'):
        //     start_offset, stop_offset = argument.timespan.offsets
        // elif hasattr(argument, '_get_timespan'):
        //     start_offset, stop_offset = argument._get_timespan().offsets
        // # TODO: remove this branch in favor of the _get_timespan above
        // #elif hasattr(argument, 'timespan'):
        // #    start_offset, stop_offset = argument.timespan().offsets
        // else:
        //     raise ValueError(argument)
        // return abjad.new(
        //     self,
        //     start_offset=start_offset,
        //     stop_offset=stop_offset,
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • prInitializeOffset
    -------------------------------------------------------------------------------------------------------- */
    prInitializeOffset { |offset|
        if ([-inf, inf].includes(offset)) { ^offset };
        ^FoscOffset(offset);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prGetOffsets
    -------------------------------------------------------------------------------------------------------- */
    *prGetOffsets { |argument|
        ^this.notYetImplemented(thisMethod);
        // if isinstance(argument, Timespan):
        //     pass
        // elif hasattr(argument, 'timespan'):
        //     argument = argument.timespan
        // elif hasattr(argument, '_get_timespan'):
        //     argument = argument._get_timespan()
        // else:
        //     raise ValueError(argument)
        // return argument._start_offset, argument._stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prGetStartOffsetAndMaybeStopOffset
    -------------------------------------------------------------------------------------------------------- */
    *prGetStartOffsetAndMaybeStopOffset { |argument|
        ^this.notYetImplemented(thisMethod);
        // if isinstance(argument, Timespan):
        //     pass
        // elif hasattr(argument, 'timespan'):
        //     argument = argument.timespan
        // elif hasattr(argument, '_get_timespan'):
        //     argument = argument._get_timespan()
        // start_offset = getattr(argument, 'start_offset', None)
        // if start_offset is None:
        //     raise ValueError(argument)
        // stop_offset = getattr(argument, 'stop_offset', None)
        // return start_offset, stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prImplementsTimespanInterface
    -------------------------------------------------------------------------------------------------------- */
    *prImplementsTimespanInterface { |timespan|
        // if (
        //     getattr(timespan, 'start_offset', 'foo') != 'foo' and
        //     getattr(timespan, 'stop_offset', 'foo') != 'foo'
        //     ):
        //     return True
        // if hasattr(timespan, '_get_timespan'):
        //     return True
        // # TODO: remove this branch in favor of the _get_timespan above
        // if hasattr(timespan, 'timespan'):
        //     return True
        // if getattr(timespan, 'timespan', 'foo') != 'foo':
        //     return True
        // return False

        if (
            timespan.respondsTo('startOffset')
            && { timespan.respondsTo('stopOffset') }
        ) {
            ^true;
        };
        if (timespan.respondsTo('prGetTimespan')) {
            ^true;
        };
        if (timespan.respondsTo('timespan')) {
            ^true;
        };
        ^false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • containsTimespanImproperly
    -------------------------------------------------------------------------------------------------------- */
    containsTimespanImproperly { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     self_start_offset <= expr_start_offset and
        //     expr_stop_offset <= self_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • curtailsTimespan
    -------------------------------------------------------------------------------------------------------- */
    curtailsTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     expr_start_offset < self_start_offset and
        //     self_start_offset <= expr_stop_offset and
        //     expr_stop_offset <= self_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • delaysTimespan
    -------------------------------------------------------------------------------------------------------- */
    delaysTimespan {
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     self_start_offset <= expr_start_offset and
        //     expr_start_offset < self_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • divideByRatio
    -------------------------------------------------------------------------------------------------------- */
    divideByRatio { |ratio|
        ^this.notYetImplemented(thisMethod);
        // if isinstance(ratio, int):
        //     ratio = ratio * (1, )
        // ratio = abjad.Ratio(ratio)
        // unit_duration = self.duration / sum(ratio.numbers)
        // part_durations = [
        //     numerator * unit_duration for numerator in ratio.numbers
        //     ]
        // start_offsets = abjad.mathtools.cumulative_sums(
        //     [self._start_offset] + part_durations,
        //     start=None,
        //     )
        // offset_pairs = abjad.sequence(start_offsets).nwise()
        // result = [type(self)(*offset_pair) for offset_pair in offset_pairs]
        // return tuple(result)
    }
    /* --------------------------------------------------------------------------------------------------------
    • getOverlapWithTimespan
    -------------------------------------------------------------------------------------------------------- */
    getOverlapWithTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // if self._implements_timespan_interface(timespan):
        //     result = abjad.Duration(
        //         sum(x.duration for x in self & timespan)
        //         )
        //     return result
    }
    /* --------------------------------------------------------------------------------------------------------
    • happensDuringTimespan
    -------------------------------------------------------------------------------------------------------- */
    happensDuringTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     expr_start_offset <= self_start_offset and
        //     self_stop_offset <= expr_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • intersectsTimespan
    -------------------------------------------------------------------------------------------------------- */
    intersectsTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     (
        //         expr_start_offset <= self_start_offset and
        //         self_start_offset < expr_stop_offset
        //     ) or (
        //         self_start_offset <= expr_start_offset and
        //         expr_start_offset < self_stop_offset
        //         )
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • isCongruentToTimespan
    -------------------------------------------------------------------------------------------------------- */
    isCongruentToTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     expr_start_offset == self_start_offset and
        //     expr_stop_offset == self_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • isTangentToTimespan
    -------------------------------------------------------------------------------------------------------- */
    isTangentToTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     self_stop_offset == expr_start_offset or
        //     expr_stop_offset == self_start_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • overlapsAllOfTimespan
    -------------------------------------------------------------------------------------------------------- */
    overlapsAllOfTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     self_start_offset < expr_start_offset and
        //     expr_stop_offset < self_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • overlapsOnlyStartOfTimespan
    -------------------------------------------------------------------------------------------------------- */
    overlapsOnlyStartOfTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     self_start_offset < expr_start_offset and
        //     expr_start_offset < self_stop_offset and
        //     self_stop_offset <= expr_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • overlapsOnlyStopOfTimespan
    -------------------------------------------------------------------------------------------------------- */
    overlapsOnlyStopOfTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     expr_start_offset <= self_start_offset and
        //     self_start_offset < expr_stop_offset and
        //     expr_stop_offset < self_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • overlapsStartOfTimespan
    -------------------------------------------------------------------------------------------------------- */
    overlapsStartOfTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     self_start_offset < expr_start_offset and
        //     expr_start_offset < self_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • overlapsStopOfTimespan
    -------------------------------------------------------------------------------------------------------- */
    overlapsStopOfTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     self_start_offset < expr_stop_offset and
        //     expr_stop_offset < self_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • reflect
    -------------------------------------------------------------------------------------------------------- */
    reflect { |axis|
        ^this.notYetImplemented(thisMethod);
        // if axis is None:
        //     axis = self.axis
        // start_distance = self._start_offset - axis
        // stop_distance = self._stop_offset - axis
        // new_start_offset = axis - stop_distance
        // new_stop_offset = axis - start_distance
        // return self.set_offsets(new_start_offset, new_stop_offset)
    }
    /* --------------------------------------------------------------------------------------------------------
    • roundOffsets
    -------------------------------------------------------------------------------------------------------- */
    roundOffsets { |multiplier, anchor='left', mustBeWellformed=true|
        ^this.notYetImplemented(thisMethod);
        // multiplier = abs(abjad.Multiplier(multiplier))
        // assert 0 < multiplier
        // new_start_offset = abjad.Offset(
        //     int(round(self._start_offset / multiplier)) * multiplier)
        // new_stop_offset = abjad.Offset(
        //     int(round(self._stop_offset / multiplier)) * multiplier)
        // if (new_start_offset == new_stop_offset) and must_be_wellformed:
        //     if anchor is enums.Left:
        //         new_stop_offset = new_stop_offset + multiplier
        //     else:
        //         new_start_offset = new_start_offset - multiplier
        // result = abjad.new(
        //     self,
        //     start_offset=new_start_offset,
        //     stop_offset=new_stop_offset,
        //     )
        // return result
    }
    /* --------------------------------------------------------------------------------------------------------
    • scale
    -------------------------------------------------------------------------------------------------------- */
    scale { |multiplier, anchor='left'|
        ^this.notYetImplemented(thisMethod);
        // multiplier = abjad.Multiplier(multiplier)
        // assert 0 < multiplier
        // new_duration = multiplier * self.duration
        // if anchor == enums.Left:
        //     new_start_offset = self._start_offset
        //     new_stop_offset = self._start_offset + new_duration
        // elif anchor == enums.Right:
        //     new_stop_offset = self._stop_offset
        //     new_start_offset = self._stop_offset - new_duration
        // else:
        //     message = 'unknown anchor direction: {!r}.'
        //     message = message.format(anchor)
        //     raise ValueError(message)
        // result = abjad.new(
        //     self,
        //     start_offset=new_start_offset,
        //     stop_offset=new_stop_offset,
        //     )
        // return result
    }
    /* --------------------------------------------------------------------------------------------------------
    • setDuration
    -------------------------------------------------------------------------------------------------------- */
    setDuration { |duration|
        ^this.notYetImplemented(thisMethod);
        // duration = abjad.Duration(duration)
        // new_stop_offset = self._start_offset + duration
        // return abjad.new(
        //     self,
        //     stop_offset=new_stop_offset,
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • setOffsets
    -------------------------------------------------------------------------------------------------------- */
    setOffsets { |startOffset, stopOffset|
        ^this.notYetImplemented(thisMethod);
        // if start_offset is not None:
        //     start_offset = abjad.Offset(start_offset)

        // if stop_offset is not None:
        //     stop_offset = abjad.Offset(stop_offset)
        // if start_offset is not None and 0 <= start_offset:
        //     new_start_offset = start_offset
        // elif start_offset is not None and start_offset < 0:
        //     new_start_offset = \
        //         self._stop_offset + abjad.Offset(start_offset)
        // else:
        //     new_start_offset = self._start_offset
        // if stop_offset is not None and 0 <= stop_offset:
        //     new_stop_offset = stop_offset
        // elif stop_offset is not None and stop_offset < 0:
        //     new_stop_offset = \
        //         self._stop_offset + abjad.Offset(stop_offset)
        // else:
        //     new_stop_offset = self._stop_offset
        // result = abjad.new(
        //     self,
        //     start_offset=new_start_offset,
        //     stop_offset=new_stop_offset,
        //     )
        // return result
    }
    /* --------------------------------------------------------------------------------------------------------
    • splitAtOffset
    -------------------------------------------------------------------------------------------------------- */
    splitAtOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // result = abjad.TimespanList()
        // if self._start_offset < offset < self._stop_offset:
        //     left = abjad.new(
        //         self,
        //         start_offset=self._start_offset,
        //         stop_offset=offset,
        //         )
        //     right = abjad.new(
        //         self,
        //         start_offset=offset,
        //         stop_offset=self._stop_offset,
        //         )
        //     result.append(left)
        //     result.append(right)
        // else:
        //     result.append(abjad.new(self))
        // return result
    }
    /* --------------------------------------------------------------------------------------------------------
    • splitAtOffsets
    -------------------------------------------------------------------------------------------------------- */
    splitAtOffsets { |offsets|
        ^this.notYetImplemented(thisMethod);
        // offsets = [abjad.Offset(offset) for offset in offsets]
        // offsets = [offset for offset in offsets
        //     if self._start_offset < offset < self._stop_offset]
        // offsets = sorted(set(offsets))
        // result = abjad.TimespanList()
        // right = abjad.new(self)
        // for offset in offsets:
        //     left, right = right.split_at_offset(offset)
        //     result.append(left)
        // result.append(right)
        // return result
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsAfterOffset
    -------------------------------------------------------------------------------------------------------- */
    startsAfterOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return offset < self._start_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsAfterTimespanStarts
    -------------------------------------------------------------------------------------------------------- */
    startsAfterTimespanStarts { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return expr_start_offset < self_start_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsAfterTimespanStops
    -------------------------------------------------------------------------------------------------------- */
    startsAfterTimespanStops { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return expr_stop_offset <= self_start_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsAtOffset
    -------------------------------------------------------------------------------------------------------- */
    startsAtOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return self._start_offset == offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsAtOrAfterOffset
    -------------------------------------------------------------------------------------------------------- */
    startsAtOrAfterOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return offset <= self._start_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsBeforeOffset
    -------------------------------------------------------------------------------------------------------- */
    startsBeforeOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return self._start_offset < offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsBeforeOrAtOffset
    -------------------------------------------------------------------------------------------------------- */
    startsBeforeOrAtOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return self._start_offset <= offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsBeforeTimespanStarts
    -------------------------------------------------------------------------------------------------------- */
    startsBeforeTimespanStarts { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return self_start_offset < expr_start_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsBeforeTimespanStops
    -------------------------------------------------------------------------------------------------------- */
    startsBeforeTimespanStops { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return self_start_offset < expr_stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsDuringTimespan
    -------------------------------------------------------------------------------------------------------- */
    startsDuringTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     expr_start_offset <= self_start_offset and
        //     self_start_offset < expr_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsWhenTimespanStarts
    -------------------------------------------------------------------------------------------------------- */
    startsWhenTimespanStarts { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return expr_start_offset == self_start_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • startsWhenTimespanStops
    -------------------------------------------------------------------------------------------------------- */
    startsWhenTimespanStops { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return self_start_offset == expr_stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsAfterOffset
    -------------------------------------------------------------------------------------------------------- */
    stopsAfterOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return offset < self._stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsAfterTimespanStarts
    -------------------------------------------------------------------------------------------------------- */
    stopsAfterTimespanStarts { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return expr_start_offset < self_stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsAfterTimespanStops
    -------------------------------------------------------------------------------------------------------- */
    stopsAfterTimespanStops { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return expr_stop_offset < self_stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsAtOffset
    -------------------------------------------------------------------------------------------------------- */
    stopsAtOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return self._stop_offset == offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsAtOrAfterOffset
    -------------------------------------------------------------------------------------------------------- */
    stopsAtOrAfterOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return offset <= self._stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsBeforeOffset
    -------------------------------------------------------------------------------------------------------- */
    stopsBeforeOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return self._stop_offset < offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsBeforeOrAtOffset
    -------------------------------------------------------------------------------------------------------- */
    stopsBeforeOrAtOffset { |offset|
        ^this.notYetImplemented(thisMethod);
        // offset = abjad.Offset(offset)
        // return self._stop_offset <= offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsBeforeTimespanStarts
    -------------------------------------------------------------------------------------------------------- */
    stopsBeforeTimespanStarts { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return self_stop_offset < expr_start_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsBeforeTimespanStops
    -------------------------------------------------------------------------------------------------------- */
    stopsBeforeTimespanStops { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return self_stop_offset < expr_stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsDuringTimespan
    -------------------------------------------------------------------------------------------------------- */
    stopsDuringTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     expr_start_offset < self_stop_offset and
        //     self_stop_offset <= expr_stop_offset
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsWhenTimespanStarts
    -------------------------------------------------------------------------------------------------------- */
    stopsWhenTimespanStarts { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return self_stop_offset == expr_start_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopsWhenTimespanStops
    -------------------------------------------------------------------------------------------------------- */
    stopsWhenTimespanStops { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return self_stop_offset == expr_stop_offset
    }
    /* --------------------------------------------------------------------------------------------------------
    • stretch
    -------------------------------------------------------------------------------------------------------- */
    stretch { |multiplier, anchor|
        ^this.notYetImplemented(thisMethod);
        // multiplier = abjad.Multiplier(multiplier)
        // assert 0 < multiplier
        // if anchor is None:
        //     anchor = self._start_offset
        // new_start_offset = (multiplier * (self._start_offset - anchor)) + anchor
        // new_stop_offset = (multiplier * (self._stop_offset - anchor)) + anchor
        // result = abjad.new(
        //     self,
        //     start_offset=new_start_offset,
        //     stop_offset=new_stop_offset,
        //     )
        // return result
    }
    /* --------------------------------------------------------------------------------------------------------
    • translate
    -------------------------------------------------------------------------------------------------------- */
    translate { |translation|
        ^this.notYetImplemented(thisMethod);
        // return self.translate_offsets(translation, translation)
    }
    /* --------------------------------------------------------------------------------------------------------
    • translateOffsets
    -------------------------------------------------------------------------------------------------------- */
    translateOffsets { |startOffsetTranslation, stopOffsetTranslation|
        ^this.notYetImplemented(thisMethod);
        // start_offset_translation = start_offset_translation or 0
        // stop_offset_translation = stop_offset_translation or 0
        // start_offset_translation = abjad.Duration(start_offset_translation)
        // stop_offset_translation = abjad.Duration(stop_offset_translation)
        // new_start_offset = self._start_offset + start_offset_translation
        // new_stop_offset = self._stop_offset + stop_offset_translation
        // return abjad.new(
        //     self,
        //     start_offset=new_start_offset,
        //     stop_offset=new_stop_offset,
        //     )
    }
    /* --------------------------------------------------------------------------------------------------------
    • trisectsTimespan
    -------------------------------------------------------------------------------------------------------- */
    trisectsTimespan { |timespan|
        ^this.notYetImplemented(thisMethod);
        // self_start_offset, self_stop_offset = self.offsets
        // expr_start_offset, expr_stop_offset = self._get_offsets(timespan)
        // return (
        //     expr_start_offset < self_start_offset and
        //     self_stop_offset < expr_stop_offset
        //     )
    }
}
