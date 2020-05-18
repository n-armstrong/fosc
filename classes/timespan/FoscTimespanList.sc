/* ------------------------------------------------------------------------------------------------------------
• FoscTimespanList

Timespan list.

t = FoscTimespanList([
    FoscTimespan(0, 3),
    FoscTimespan(5, 13),
    FoscTimespan(6, 10),
    FoscTimespan(8, 9),
    FoscTimespan(15, 23),
    FoscTimespan(16, 21),
    FoscTimespan(17, 19),
    FoscTimespan(19, 20),
    FoscTimespan(25, 30),
    FoscTimespan(26, 29),
    FoscTimespan(32, 34),
    FoscTimespan(34, 37),
]);

t.show; 

• TODO: 'scale' not yet implemented for FoscTimespanList:show, but is implemented for 'illustrate'
show(t, scale: 0.5); 


f = t.illustrate(scale: 0.5);
f.show;
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
    }
    /* --------------------------------------------------------------------------------------------------------
    • allAreNonoverlapping

    Is true when all timespans are nonoverlapping.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    allAreNonoverlapping {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • allAreWellformed

    Is true when all timespans are wellformed.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    allAreWellformed {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • axis

    Gets axis defined as equal to arithmetic mean of start- and stop-offsets.

    Return offset or nil.
    -------------------------------------------------------------------------------------------------------- */
    axis {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • duration

    Gets duration of timespan list.

    Returns duration.
    -------------------------------------------------------------------------------------------------------- */
    duration {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSorted

    Is true when timespans are in time order.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    isSorted {
        ^this.notYetImplemented(thisMethod);
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
        ^FoscTimespan(this.startOffset, this.stopOffset);
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
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeLogicalAnd

    Computes logical AND of timespans.

    Same as setwise intersection.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    computeLogicalAnd {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeLogicalOr

    Computes logical OR of timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    computeLogicalOr {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeLogicalXor

    Computes logical XOR of timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    computeLogicalXor {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeOverlapFactor

    Computes overlap factor of timespans.

    Returns multiplier.


    l = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(5, 15),
        FoscTimespan(20, 25),
        FoscTimespan(20, 30),
    ]);


    • Example 1

    Computes overlap factor across the entire list.

    l.computeOverlapFactor.cs;


    • Example 2

    Computes overlap factor within a specific timespan.

    l.computeOverlapFactor(FoscTimespan(-15, 0)).cs;


    • Example 3

    Computes overlap factor.

    l.computeOverlapFactor(FoscTimespan(-10, 5)).cs;


    • Example 4

    Computes overlap factor.

    l.computeOverlapFactor(FoscTimespan(-5, 10)).cs;


    • Example 5

    Computes overlap factor.

    l.computeOverlapFactor(FoscTimespan(0, 15)).cs;


    • Example 6

    Computes overlap factor.

    l.computeOverlapFactor(FoscTimespan(5, 20)).cs;


    • Example 7

    Computes overlap factor.

    l.computeOverlapFactor(FoscTimespan(10, 25)).cs;


    • Example 8

    Computes overlap factor.

    l.computeOverlapFactor(FoscTimespan(15, 30)).cs;
    -------------------------------------------------------------------------------------------------------- */
    computeOverlapFactor { |timespan|
        var timeRelation, timespans, totalOverlap, overlapFactor;
        if (timespan.isNil) { timespan = this.timespan };
        timespans = this.select { |each| each.intersectsTimespan(timespan) };
        totalOverlap = timespans.items.collect { |each| each.getOverlapWithTimespan(timespan) }.sum;
        overlapFactor = totalOverlap / timespan.duration;
        ^overlapFactor;
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeOverlapFactorMapping

    Computes overlap factor for each consecutive offset pair in timespans.

    Returns mapping.
    -------------------------------------------------------------------------------------------------------- */
    computeOverlapFactorMapping {
        var mapping, offsets, timespan, overlapFactor;
        mapping = ();
        offsets = this.countOffsets.sort;
        offsets.doAdjacentPairs { |startOffset, stopOffset|
            timespan = FoscTimespan(startOffset, stopOffset);
            overlapFactor = this.computerOverlapFactor(timespan: timespan);
            mapping[timespan] = overlapFactor;
        };
        ^mapping; // • TODO: not a sorted dictionary
    }
    /* --------------------------------------------------------------------------------------------------------
    • countOffsets

    Counts offsets.

    Returns counter.
    -------------------------------------------------------------------------------------------------------- */
    countOffsets {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • explode

    Explodes timespans into timespan lists, avoiding overlap, and distributing density as evenly as possible.

    Returns timespan lists.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(5, 13),
        FoscTimespan(6, 10),
        FoscTimespan(8, 9),
        FoscTimespan(15, 23),
        FoscTimespan(16, 21),
        FoscTimespan(17, 19),
        FoscTimespan(19, 20),
        FoscTimespan(25, 30),
        FoscTimespan(26, 29),
        FoscTimespan(32, 34),
        FoscTimespan(34, 37),
    ]);

    m = l.explode;

    m.do { |timespanList|
        Post.nl;
        timespanList.do { |ts| [ts.startOffset.cs, ts.stopOffset.cs].postln };
    }; "";
    -------------------------------------------------------------------------------------------------------- */
    explode { |inventoryCount|
        var boundingTimespan, globalOverlapFactors, emptyTimespanPairs, resultTimespanLists, resultTimespans;
        var currentOverlapFactor, nonOverlappingTimespanLists, overlappingTimespanLists, localOverlapFactor;
        var emptyTimespans, globalOverlapFactor, i;

        assert([Integer, Nil].any { |type| inventoryCount.isKindOf(type) });
        if (inventoryCount.isInteger) { assert(inventoryCount > 0) };
        boundingTimespan = this.timespan;
        globalOverlapFactors = [];
        emptyTimespanPairs = [];
        resultTimespanLists = [];


        if (inventoryCount.notNil) {
            inventoryCount.do { |i|
                globalOverlapFactors = globalOverlapFactors.add(0);
                resultTimespans = this.species.new([]);
                emptyTimespanPairs = emptyTimespanPairs.add([i, resultTimespans]);
                resultTimespanLists = resultTimespanLists.add(resultTimespans);
            };
        };

        this.do { |currentTimespan|
            currentOverlapFactor = currentTimespan.duration / boundingTimespan.duration;

            if (emptyTimespanPairs.notEmpty) {
                # i, emptyTimespans = emptyTimespanPairs.pop;
                emptyTimespans = emptyTimespans.add(currentTimespan);
                globalOverlapFactors[i] = currentOverlapFactor;
            } {
                nonOverlappingTimespanLists = List[];
                overlappingTimespanLists = List[];  

                resultTimespanLists.do { |resultTimespans, i|

                    localOverlapFactor = resultTimespans.computeOverlapFactor(currentTimespan);
                    globalOverlapFactor = globalOverlapFactors[i];

                    if (localOverlapFactor < 1) {
                        nonOverlappingTimespanLists.add([i, globalOverlapFactor]);
                    } {
                        overlappingTimespanLists.add([i, localOverlapFactor, globalOverlapFactor]);
                    };
                };

                nonOverlappingTimespanLists = nonOverlappingTimespanLists.sort { |a, b| a[1] < b[1] };
                overlappingTimespanLists = overlappingTimespanLists.sortN(startIndex: 1);

                if (nonOverlappingTimespanLists.isEmpty && { inventoryCount.isNil }) {
                    resultTimespans = this.species.new([currentTimespan]);
                    globalOverlapFactors = globalOverlapFactors.add(currentOverlapFactor);
                    resultTimespanLists = resultTimespanLists.add(resultTimespans);
                } {

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

        ^resultTimespanLists;
    }
    /* --------------------------------------------------------------------------------------------------------
    • getTimespanThatSatisfiesTimeRelation

    Gets timespan that satisifies 'timeRelation'.

    Returns timespan when timespan list contains exactly one timespan that satisfies 'timeRelation'.

    Raises exception when timespan list contains no timespan that satisfies 'timeRelation'.

    Raises exception when timespan list contains more than one timespan that satisfies 'timeRelation'.
    -------------------------------------------------------------------------------------------------------- */
    getTimespanThatSatisfiesTimeRelation { |timeRelation|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • getTimespansThatSatisfyTimeRelation

    • NOTE: use 'select' instead


    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    m = FoscTimespan(8, 15);

    z = l.select { |timespan| timespan.intersectsTimespan(m) };
    z.items[0].startOffset.str;
    -------------------------------------------------------------------------------------------------------- */
    getTimespansThatSatisfyTimeRelation { |timeRelation|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • hasTimespanThatSatisfiesTimeRelation

    Is true when timespan list has timespan that satisfies 'timeRelation'.

    Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    hasTimespanThatSatisfiesTimeRelation { |timeRelation|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • partition

    Partitions timespans into timespan lists.

    Returns zero or more timespan lists.
    -------------------------------------------------------------------------------------------------------- */
    partition { |includeTangentTimespans=false|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • reflect

    Reflects timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    reflect { |axis|    
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • removeDegenerateTimespans

    Removes degenerate timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    removeDegenerateTimespans {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • repeatToStopOffset

    Repeats timespans to 'stopOffset'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    repeatToStopOffset { |stopOffset|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • rotate

    Rotates by 'count' contiguous timespans.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    rotate { |count|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • roundOffsets

    Rounds offsets of timespans in list to multiples of 'multiplier'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    roundOffsets { |multiplier, anchor='left', mustBeWellformed=true|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • scale

    Scales timespan by 'multiplier' relative to 'anchor'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    scale { |multiplier, anchor='left'|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • splitAtOffset

    Splits timespans at 'offset'.

    Returns timespan_lists.
    -------------------------------------------------------------------------------------------------------- */
    splitAtOffset { |offset|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • splitAtOffsets

    Splits timespans at 'offsets'.

    Returns one or more timespan lists.
    -------------------------------------------------------------------------------------------------------- */
    splitAtOffsets { |offsets|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • stretch

    Stretches timespans by 'multiplier' relative to 'anchor'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    stretch { |multiplier, anchor|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • translate

    Translates timespans by 'translation'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    translate { |translation|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • translateOffsets

    Translates timespans by 'start_offset_translation' and \'stop_offset_translation'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    translateOffsets { |startOffsetTranslation, stopOffsetTranslation|
        ^this.notYetImplemented(thisMethod);
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

    f = t.illustrate(scale: 0.5);
    f.format;
    f.show;
    
    t.show;
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |key, range, sortkey, scale=1|
        var minimum, maximum, postscriptScale, postscriptXOffset, timespanLists, markup, val, markups;
        var timespans, valueMarkup, vspaceMarkup, timespanMarkup;

        if (this.items.isEmpty) { ^FoscMarkup().null.illustrate };

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

        minimum = FoscOffset(minimum).asFloat;
        maximum = FoscOffset(maximum).asFloat;
        postscriptScale = 150 / (maximum - minimum);
        postscriptScale = postscriptScale * scale;
        postscriptXOffset = (minimum * postscriptScale) - 1;
        
        if (key.isNil) {
            markup = FoscTimespanList.prMakeTimespanListMarkup(
                this, postscriptXOffset, postscriptScale, sortkey: sortkey);
        } {
            timespanLists = ();
            this.do { |timespan|
                val = if (timespan.respondsTo(key)) { timespan.perform(key) };
                if (timespanLists[val].isNil) {
                    timespanLists[val] = this.species.new;
                };
                timespanLists[val].add(timespan);
            };
            markups = [];

            // • TODO: change to: timespanLists.keys.asArray.sort.do 
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

        ^markup.illustrate;
    }
    /* --------------------------------------------------------------------------------------------------------
    • invert

    Inverts timespans.

    Returns new timespan list.
    -------------------------------------------------------------------------------------------------------- */
    invert {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • sect (abjad: __and__)

    Keeps material that intersects 'timespan'.

    Operates in place and returns timespan list.
    -------------------------------------------------------------------------------------------------------- */
    union { |timespan|
        ^this.notYetImplemented(thisMethod);
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
    
    m = FoscTimespanList.prMakeTimespanListMarkup(t, -1, 15);
    m.format;
    -------------------------------------------------------------------------------------------------------- */
    *prMakeTimespanListMarkup { |timespans, postscriptXOffset, postscriptScale, drawOffsets=true, sortkey|
        var explodedTimespanLists, sortedTimespanLists, key, val, ps, offsetMapping, height, postscriptYOffset;
        var markup, level, xOffset, xExtent, yExtent, linesMarkup, fractionMarkups, offset, numerator;
        var denominator, fraction, xTranslation, fractionMarkup;
        

        explodedTimespanLists = [];
        if (sortkey.isNil) {
            explodedTimespanLists = explodedTimespanLists.addAll(timespans.explode); 
        } {

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

        ps = FoscPostscript();
        ps = ps.setlinewidth(0.2);  
        offsetMapping = ();
        height = ((explodedTimespanLists.size - 1) * 3) + 1;

        explodedTimespanLists.do { |timespans, level|
            postscriptYOffset = height - (level * 3) - 0.5;
            timespans.do { |timespan|
                offsetMapping[timespan.startOffset] = level;
                offsetMapping[timespan.stopOffset] = level;
                ps = ps ++ timespan.prAsPostscript(postscriptXOffset, postscriptYOffset, postscriptScale);
            };
        };
        
        if (drawOffsets.not) {
            markup = FoscMarkup.postscript(ps);
            ^markup;
        };

        ps = ps.setlinewidth(0.1);
        ps = ps.setdash(#[0.1, 0.2]);

        offsetMapping.keys.asArray.sort.do { |offset|
            level = offsetMapping[offset];
            xOffset = offset.asFloat * postscriptScale;
            xOffset = xOffset - postscriptXOffset;
            ps = ps.moveto(xOffset, height + 1.5);
            ps = ps.lineto(xOffset, height - (level * 3));
            ps = ps.stroke;
        };

        ps = ps.moveto(0, 0);
        ps = ps.setgray(0.99);
        ps = ps.rlineto(0, 0.01);
        ps = ps.stroke;

        xExtent = timespans.stopOffset.asFloat;
        xExtent = xExtent * postscriptScale;
        xExtent = xExtent + postscriptXOffset;
        xExtent = [0, xExtent];
        yExtent = [0, height + 1.5];

        linesMarkup = FoscMarkup.postscript(ps);
        linesMarkup = linesMarkup.padToBox(xExtent, yExtent);

        fractionMarkups = [];

        offsetMapping.keys.asArray.sort.do { |offset|
            offset = FoscMultiplier(offset);
            # numerator, denominator = [offset.numerator, offset.denominator];
            fraction = FoscMarkup.fraction(numerator, denominator);
            fraction = fraction.centerAlign.fontSize(-3).sans;
            xTranslation = offset.asFloat * postscriptScale;
            xTranslation = xTranslation - postscriptXOffset;
            fraction = fraction.translate([xTranslation, 1]);
            fractionMarkups = fractionMarkups.add(fraction);
        };

        fractionMarkup = FoscMarkup.overlay(fractionMarkups);
        markup = FoscMarkup.column([fractionMarkup, linesMarkup]);
        
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
