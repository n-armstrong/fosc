/* ------------------------------------------------------------------------------------------------------------
• FoscTimespanList

Timespan list.


Nonverlapping timespan list.

t = FoscTimespanList([
    FoscTimespan(0, 3),
    FoscTimespan(3, 6),
    FoscTimespan(6, 10)
]);

t.show(scale: 0.5); 


Nonverlapping timespan list.

t = FoscTimespanList([
    FoscTimespan(0, 10),
    FoscTimespan(10, 20),
    FoscTimespan(30, 40)
]);

t.show(scale: 0.5); 


Overlapping timespan list.

t = FoscTimespanList([
    FoscTimespan(0, 6),
    FoscTimespan(5, 12),
    FoscTimespan(-2, 8),
    FoscTimespan(15, 20),
    FoscTimespan(24, 30)
]);

t.show(scale: 0.5); 


Overlapping timespan list.

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

t.show(scale: 0.5); 
------------------------------------------------------------------------------------------------------------ */
FoscTimespanList : FoscTypedList {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |items|
        ^super.new(items, FoscTimespan);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(10, 20),
        FoscTimespan(30, 40)
    ]);

    t.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    Illustrates timespans.

    Returns LilyPond file.


    • Example 1
    
    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(4, 6),
        FoscTimespan(6, 10),
    ]);

    f = t.illustrate(scale: 0.5);
    f.format;
    f.show;
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |key, range, sortkey, scale=1, drawOffsets=true|
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
                this, postscriptXOffset, postscriptScale, sortkey: sortkey, drawOffsets: drawOffsets);
        } {
            timespanLists = ();
            this.do { |timespan|
                if (timespan.respondsTo(key)) {
                    val = timespan.perform(key);
                } {
                    // '^throw' not caught here
                    error("%:illustrate: timespan does not respond to: '%'".format(this.species, key));
                    ^nil;
                };
                if (timespanLists[val].isNil) {
                    timespanLists[val] = this.species.new;
                };
                timespanLists[val].add(timespan);
            };
            markups = [];
            timespanLists.asSortedArray.do { |pair, i|
                # val, timespans = pair;
                timespans = timespans.sort;
                if (0 < i) {
                    vspaceMarkup = FoscMarkup.vspace(0.5);
                    markups = markups.add(vspaceMarkup);
                };
                valueMarkup = FoscMarkup("%:".format(val));
                valueMarkup = FoscMarkup.line([valueMarkup]);
                valueMarkup = valueMarkup.sans.fontSize(-2);
                markups = markups.add(valueMarkup);
                vspaceMarkup = FoscMarkup.vspace(0.5);
                markups = markups.add(vspaceMarkup);
                timespanMarkup = FoscTimespanList.prMakeTimespanListMarkup(
                    timespans, postscriptXOffset, postscriptScale, sortkey: sortkey, drawOffsets: drawOffsets);
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

    t = FoscTimespanList([
        FoscTimespan(-2, 8),
        FoscTimespan(15, 20),
        FoscTimespan(24, 30),
    ]);

    t.show(scale: 0.5);

    t.invert;

    t.show(range: #[-2,30], scale: 0.5);
    -------------------------------------------------------------------------------------------------------- */
    invert {
        var result;
        
        result = this.species.new();
        result.append(this.timespan);
        
        this.do { |timespan|
            result = result - timespan;
        };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • show
    -------------------------------------------------------------------------------------------------------- */
    show { |key, range, sortkey, scale=1|
        this.illustrate(key, range, sortkey, scale).show;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SET OPERATIONS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • - (abjad: __sub__, -)

    Deletes material from receiver that intersects 'timespan'.

    Operates in place and returns timespan list.

    t = FoscTimespanList([
        FoscTimespan(0, 16),
        FoscTimespan(5, 12),
        FoscTimespan(-2, 8),
    ]);

    t.show(scale: 0.5);

    a = FoscTimespan(5, 10);
    t - a;

    t.show(scale: 0.5);
    -------------------------------------------------------------------------------------------------------- */
    - { |timespan|
        var newTimespans, result;

        newTimespans = [];

        this.do { |currentTimespan|
            result = (currentTimespan - timespan).collection;
            newTimespans = newTimespans.addAll(result);
        };

        newTimespans.sort;
        collection = newTimespans;

        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • sect (abjad: __and__)

    Keeps material from receiver that intersects 'timespan'.

    Operates in place and returns timespan list.

    t = FoscTimespanList([
        FoscTimespan(0, 16),
        FoscTimespan(5, 12),
        FoscTimespan(-2, 8),
    ]);

    t.show(scale: 0.5);

    a = FoscTimespan(5, 10);
    t.sect(a);

    t.show(range: #[-2,16], scale: 0.5);
    -------------------------------------------------------------------------------------------------------- */
    sect { |timespan|
        var newTimespans, result;

        newTimespans = [];

        this.do { |currentTimespan|
            result = currentTimespan.sect(timespan).collection;
            newTimespans = newTimespans.addAll(result);
        };

        newTimespans.sort;
        collection = newTimespans;

        ^this;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • allAreContiguous

    Is true when all timespans are contiguous.

    Returns true or false.


    Is true when timespans are contiguous.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10),
    ]);

    t.allAreContiguous;


    Is false when timespans are not contiguous.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(4, 6),
        FoscTimespan(6, 10),
    ]);

    t.allAreContiguous;


    Is true when receiver is empty.

    t = FoscTimespanList();
    t.allAreContiguous;
    -------------------------------------------------------------------------------------------------------- */
    allAreContiguous {
        var timespans, lastStopOffset;

        if (this.size <= 1) { ^true };
        timespans = collection.sort;
        lastStopOffset = timespans[0].stopOffset;
        
        timespans[1..].do { |timespan|
            if (timespan.startOffset != lastStopOffset) { ^false };
            lastStopOffset = timespan.stopOffset;
        };

        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • allAreNonoverlapping

    Is true when all timespans are nonoverlapping.

    Returns true or false.


    Is true when timespans are nonoverlapping.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10),
    ]);

    t.allAreNonoverlapping;


    Is false when timespans are overlapping.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(2, 6),
        FoscTimespan(6, 10),
    ]);

    t.allAreNonoverlapping;


    Is true when receiver is empty.

    t = FoscTimespanList();
    t.allAreNonoverlapping;
    -------------------------------------------------------------------------------------------------------- */
    allAreNonoverlapping {
        var timespans, lastStopOffset;

        if (this.size <= 1) { ^true };
        timespans = collection.sort;
        lastStopOffset = timespans[0].stopOffset;
        
        timespans[1..].do { |timespan|
            if (timespan.startOffset < lastStopOffset) { ^false };
            if (lastStopOffset < timespan.stopOffset) { lastStopOffset = timespan.stopOffset };
        };

        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • allAreWellformed

    Is true when all timespans are wellformed.

    Returns true or false.


    Is true when all timespans are well-formed.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10),
    ]);

    t.allAreWellformed;


    Is true when all timespans are well-formed.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(2, 6),
        FoscTimespan(6, 10),
    ]);

    t.allAreWellformed;


    Is true when receiver is empty.

    t = FoscTimespanList();
    t.allAreWellformed;
    -------------------------------------------------------------------------------------------------------- */
    allAreWellformed {
        ^collection.every { |each| this.prGetTimespan(each).isWellFormed };
    }
    /* --------------------------------------------------------------------------------------------------------
    • axis

    Gets axis defined as equal to arithmetic mean of start- and stop-offsets.

    Return offset or nil.

    
    Get axis.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10),
    ]);

    t.axis.cs;


    Returns nil when receiver is empty.

    t = FoscTimespanList();
    t.axis;
    -------------------------------------------------------------------------------------------------------- */
    axis {
        if (collection.notEmpty) {
            ^(this.startOffset + this.stopOffset) / 2;
        };
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • duration

    Gets duration of timespan list.

    Returns duration.


    Get duration.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10),
    ]);

    t.duration.cs;


    t = FoscTimespanList([
        FoscTimespan(0, 16),
        FoscTimespan(5, 12),
        FoscTimespan(-2, 8),
        FoscTimespan(15, 20),
        FoscTimespan(24, 30)
    ]);

    t.duration.cs;

    
    Returns zero when timespan list is empty.

    t = FoscTimespanList();
    t.duration.cs;
    -------------------------------------------------------------------------------------------------------- */
    duration {
        if (this.stopOffset != inf && { this.startOffset != -inf }) {
            ^(this.stopOffset - this.startOffset);
        } {
            ^FoscDuration(0);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSorted

    Is true when timespans are in time order.

    Returns true or false.

    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    t.isSorted;


    t = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(6, 10),
        FoscTimespan(3, 6)
    ]);

    t.isSorted;
    -------------------------------------------------------------------------------------------------------- */
    isSorted {
        if (this.size < 2) { ^true };
        
        collection.doAdjacentPairs { |a, b|
            if (b.startOffset < a.startOffset) { ^false };
            if (a.startOffset == b.startOffset && { b.stopOffset < a.stopOffset } ) { ^false };
        };
        
        ^true;
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
    • computeSect (abjad: compute_logical_and)

    Computes setwise intersection (logical AND) of timespans.

    Operates in place and returns timespan list.

    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(5, 12)
    ]);

    t.show(scale: 0.5);

    t.computeSect;
    t.show(range: #[0,12], scale: 0.5);


    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(5, 12),
        FoscTimespan(-2, 8)
    ]);

    t.show(range: #[-2,12], scale: 0.5);

    t.computeSect;
    t.show(range: #[0,12], scale: 0.5);
    -------------------------------------------------------------------------------------------------------- */
    computeSect {
        var result, timespans;
        
        if (1 < this.size) {
            result = this[0];
            
            this.do { |timespan|
                if (timespan.intersectsTimespan(result).not) {
                    collection = [];
                    ^this;
                } {
                    timespans = result.sect(timespan);
                    result = timespans[0];
                };
                collection = [result];
            };
        };
        
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeUnion (abjad: computer_logical_or)

    Computes union (logical OR) of timespans.

    Operates in place and returns timespan list.


    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(5, 12)
    ]);

    t.show(scale: 0.5);

    t.computeUnion;
    t.show(range: #[0,12], scale: 0.5);


    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(5, 12),
        FoscTimespan(-2, 8)
    ]);

    t.show(range: #[-2,12], scale: 0.5);

    t.computeUnion;
    t.show(range: #[-2,12], scale: 0.5);
    -------------------------------------------------------------------------------------------------------- */
    computeUnion {
        var result, timespans;

        result = [];

        if (this.notEmpty) {
            result = [this[0]];
            
            this[1..].do { |timespan|
                if (result.last.prCanFuse(timespan)) {
                    timespans = result.last.union(timespan).collection;
                    result.pop;
                    result = result.addAll(timespans)
                } {
                    result = result.add(timespan)
                };
                collection = result;
            };
        };

        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • computeSymmetricDifference (abjad: compute_logical_xor)

    Computes symmetric difference (logical XOR) of timespans.

    Operates in place and returns timespan list.


    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(5, 12)
    ]);

    t.show(scale: 0.5);

    t.computeSymmetricDifference;
    t.cs;
    t.show(range: #[0,12], scale: 0.5);



    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(5, 12),
        FoscTimespan(-2, 2)
    ]);

    t.cs;

    t.computeSymmetricDifference;
    t.cs;



    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(4, 8),
        FoscTimespan(2, 6)
    ]);

    t.cs;

    t.computeSymmetricDifference;
    t.cs;


    t = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(0, 10)
    ]);

    t.cs;

    t.computeSymmetricDifference;
    t.cs;
    -------------------------------------------------------------------------------------------------------- */
    computeSymmetricDifference {
        var allFragments, timespan1Fragments, revisedTimespan1Fragments, timespan1Fragment, result;

        allFragments = [];

        this.do { |timespan1, i|
            
            timespan1Fragments = [timespan1];
            
            this.do { |timespan2, j|
                if (i != j) {

                    revisedTimespan1Fragments = [];
                    
                    timespan1Fragments.do { |timespan1Fragment|
                        if (timespan2.intersectsTimespan(timespan1Fragment)) {
                            result = (timespan1Fragment - timespan2).collection;
                            revisedTimespan1Fragments = revisedTimespan1Fragments.addAll(result);
                        } {
                            revisedTimespan1Fragments = revisedTimespan1Fragments.add(timespan1Fragment);
                        };
                    };
                    
                    timespan1Fragments = revisedTimespan1Fragments;
                };
            };
            
            allFragments = allFragments.addAll(timespan1Fragments);
        };

        collection = allFragments;
        this.sort;
            
        ^this;
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


    Computs overlap factor mapping.

    l = FoscTimespanList([
        FoscTimespan(0, 10),
        FoscTimespan(5, 15),
        FoscTimespan(20, 25),
        FoscTimespan(20, 30),
    ]);
    
    m = l.computeOverlapFactorMapping;

    m.asSortedArray.do { |each| # a, b = each; a.cs.post; ": ".post; b.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    computeOverlapFactorMapping {
        var mapping, offsets, timespan, overlapFactor;
        
        mapping = ();
        offsets = this.countOffsets.collect { |each| each[0] };

        offsets.do { |each| each.cs.postln };
        
        offsets.doAdjacentPairs { |startOffset, stopOffset|
            timespan = FoscTimespan(startOffset, stopOffset);
            overlapFactor = this.computeOverlapFactor(timespan: timespan);
            mapping[timespan] = overlapFactor;
        };
        
        ^mapping;
    }
    /* --------------------------------------------------------------------------------------------------------
    • countOffsets

    Counts offsets.

    Returns an array of offsets and counts;


    a = FoscTimespanList([
        FoscTimespan(-2, 6),
        FoscTimespan(0, 6),
        FoscTimespan(5, 6),
        FoscTimespan(10, 12),
        FoscTimespan(10, 16)
    ]);

    b = a.countOffsets;
    b.do { |each| [each[0].cs, each[1]].postln };
    -------------------------------------------------------------------------------------------------------- */
    countOffsets {
        var offsets, result;

        offsets = [];
        this.do { |timespan| offsets = offsets.addAll([timespan.startOffset, timespan.stopOffset]) };
        offsets = offsets.sort;
        offsets = offsets.separate { |a, b| a != b };
        result = offsets.collect { |each| [each[0], each.size] };

        ^result;
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
    m.do { |timespanList|  timespanList.cs.postln; Post.nl }; "";


    • TEST

    l = FoscTimespanList([
        FoscTimespan(0, 6),
        FoscTimespan(5, 12),
        FoscTimespan(-2, 8),
        FoscTimespan(15, 20),
        FoscTimespan(24, 30)
    ]);

    m = l.explode;
    m.do { |timespanList|  timespanList.cs.postln; Post.nl }; "";

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

                    if (localOverlapFactor == 0) {
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
    • getTimespansThatSatisfyTimeRelation

    • NOTE: use 'select' instead


    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    m = FoscTimespan(8, 15);

    z = l.select { |timespan| timespan.intersectsTimespan(m) };
    z.cs;
    -------------------------------------------------------------------------------------------------------- */
    getTimespansThatSatisfyTimeRelation { |timeRelation|
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • partition

    Partitions timespans into timespan lists.

    Returns zero or more timespan lists.


    Partition timespans.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    m = l.partition;
    m.do { |timespanList| timespanList.cs.postln };


    Partition timespans.

    l = FoscTimespanList([
        FoscTimespan(0, 16),
        FoscTimespan(5, 12),
        FoscTimespan(-2 ,8),
        FoscTimespan(15, 20),
        FoscTimespan(24, 30)
    ]);

    m = l.partition;
    m.do { |timespanList| timespanList.cs.postln };


    Treats tangent timespans as part of the same group when 'includeTangentTimespans' is true.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    m = l.partition(includeTangentTimespans: true);
    m.do { |timespanList| timespanList.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    partition { |includeTangentTimespans=false|
        var timespanLists, timespans, currentList, latestStopOffset;

        if (this.isEmpty) { ^[] };
        timespanLists = [];
        timespans = collection.deepCopy.sort;
        currentList = this.species.new([timespans[0]]);
        latestStopOffset = currentList[0].stopOffset;

        timespans[1..].do { |currentTimespan|
            case
            { currentTimespan.startOffset < latestStopOffset } {
                currentList = currentList.add(currentTimespan);
            }
            { includeTangentTimespans && { currentTimespan.startOffset == latestStopOffset } } {
                currentList = currentList.add(currentTimespan);
            }
            {
                timespanLists = timespanLists.add(currentList);
                currentList = this.species.new([currentTimespan]);
            };

            if (latestStopOffset < currentTimespan.stopOffset) {
                latestStopOffset = currentTimespan.stopOffset;
            };
        };

        if (currentList.notEmpty) { timespanLists = timespanLists.add(currentList) };

        ^timespanLists;
    }
    /* --------------------------------------------------------------------------------------------------------
    • reflect

    Reflects timespans.

    Operates in place and returns timespan list.


    Reflects timespans about timespan list axis.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.show(scale: 0.5);

    l.reflect;
    l.show(scale: 0.5);


    Reflects timespans about arbitrary axis.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.show(range: #[0,30], scale: 0.5);

    l.reflect(15);
    l.show(range: #[0,30], scale: 0.5);
    -------------------------------------------------------------------------------------------------------- */
    reflect { |axis|    
        var timespans;

        if (axis.isNil) { axis = this.axis };

        timespans = [];
        
        this.do { |timespan|
            timespan = timespan.reflect(axis);
            timespans = timespans.add(timespan);
        };

        timespans = timespans.reverse;
        collection = timespans;
        
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • removeDegenerateTimespans

    Removes degenerate timespans.

    Operates in place and returns timespan list.


    l = FoscTimespanList([
        FoscTimespan(5, 5),
        FoscTimespan(5, 10),
        FoscTimespan(5, 25)
    ]);

    l.removeDegenerateTimespans;
    l.cs;
    -------------------------------------------------------------------------------------------------------- */
    removeDegenerateTimespans {
        var timespans;
        
        timespans = this.items.select { |timespan| timespan.isWellFormed };
        collection = timespans;
        
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • roundOffsets

    Rounds offsets of timespans in list to multiples of 'multiplier'.

    Operates in place and returns timespan list.


    Rounds offsets relative to timespan list start offset.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.roundOffsets(3);
    l.cs;

    
    Rounds offsets relative to timespan list stop offset.

    l = FoscTimespanList([
        FoscTimespan(0, 2),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.roundOffsets(5, anchor: 'right');
    l.cs;
    -------------------------------------------------------------------------------------------------------- */
    roundOffsets { |multiplier, anchor='left', mustBeWellformed=true|
        var timespans;
        
        timespans = [];
        
        this.do { |timespan|
            timespan = timespan.roundOffsets(multiplier, anchor, mustBeWellformed);
            timespans = timespans.add(timespan);
        };
        
        collection = timespans;
        
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • scale

    Scales timespan by 'multiplier' relative to 'anchor'.

    Operates in place and returns timespan list.


    Scales timespans relative to timespan list start offset.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.scale(2);
    l.cs;


    Scales timespans relative to timespan list stop offset.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.scale(2, anchor: 'right');
    l.cs;
    -------------------------------------------------------------------------------------------------------- */
    scale { |multiplier, anchor='left'|
        var timespans;
        
        timespans = [];
        
        this.do { |timespan|
            timespan = timespan.scale(multiplier, anchor);
            timespans = timespans.add(timespan);
        };
        
        collection = timespans;
        
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • sort

    l = FoscTimespanList([
        FoscTimespan(6, 10),
        FoscTimespan(0, 3),
        FoscTimespan(3, 6)
    ]);

    l.sort.cs;
    -------------------------------------------------------------------------------------------------------- */
    sort { |func|
        func = func ?? { { |a, b| a.startOffset < b.startOffset } };
        ^super.sort(func);
    }
    /* --------------------------------------------------------------------------------------------------------
    • splitAtOffset

    Splits timespans at 'offset'.

    Returns array of FoscTimespanLists.


    Splits at offset.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    m = l.splitAtOffset(4);
    m.do { |timespanList| timespanList.cs.postln };


    Splits at offset.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    m = l.splitAtOffset(-1);
    m.do { |timespanList| timespanList.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    splitAtOffset { |offset|
        var beforeList, duringList, afterList, beforeTimespan, afterTimespan;

        offset = FoscOffset(offset);
        beforeList = this.species.new; 
        duringList = this.species.new; 
        afterList = this.species.new;        
        
        this.do { |timespan|
            case
            { timespan.stopOffset <= offset } {
                beforeList.add(timespan);
            }
            { offset <= timespan.startOffset } {
                afterList.add(timespan);
            }
            {
                duringList.add(timespan);
            };
        };

        duringList.do { |timespan|
            # beforeTimespan, afterTimespan = timespan.splitAtOffset(offset);
            beforeList.add(beforeTimespan);
            afterList.add(afterTimespan);
        };

        beforeList.sort;
        afterList.sort;

        ^[beforeList, afterList];
    }
    /* --------------------------------------------------------------------------------------------------------
    • splitAtOffsets

    Splits timespans at 'offsets'.

    Returns one or more timespan lists.


    Splits at offset3.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(4, 10),
        FoscTimespan(15, 20)
    ]);

    m = l.splitAtOffsets(#[-1,3,6,12,13]);
    m.do { |timespanList| timespanList.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    splitAtOffsets { |offsets|
        var timespanLists, shards;

        timespanLists = [this];
        if (this.isEmpty) { ^timespanLists };
        offsets = offsets.as(Set).asArray.sort;
        offsets = offsets.select { |offset| this.startOffset < offset && { offset < this.stopOffset } };

        offsets.do { |offset|
            shards = timespanLists.last.splitAtOffset(offset);
            if (shards.notEmpty) {
                shards = shards.select { |each| each.notEmpty };
                timespanLists = timespanLists[..(timespanLists.lastIndex - 1)];
                timespanLists = timespanLists.addAll(shards);
            };
        };

        ^timespanLists;
    }
    /* --------------------------------------------------------------------------------------------------------
    • stretch

    Stretches timespans by 'multiplier' relative to 'anchor'.

    Operates in place and returns timespan list.


    Stretches timespans relative to timespan list start offset.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.stretch(2);
    l.cs;


    Stretches timespans relative to timespan list stop offset.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.stretch(2, anchor: l.stopOffset);
    l.cs;
    -------------------------------------------------------------------------------------------------------- */
    stretch { |multiplier, anchor|
        var timespans;
        
        timespans = [];
        anchor = anchor ?? { this.startOffset };
        
        this.do { |timespan|
            timespan = timespan.stretch(multiplier, anchor);
            timespans = timespans.add(timespan);
        };
        
        collection = timespans;
        
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • translate

    Translates timespans by 'translation'.

    Operates in place and returns timespan list.


    Translate timespan by offset 50.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.translate(50);
    l.cs;
    -------------------------------------------------------------------------------------------------------- */
    translate { |translation|
        ^this.translateOffsets(translation, translation);
    }
    /* --------------------------------------------------------------------------------------------------------
    • translateOffsets

    Translates timespans by 'startOffsetTranslation' and 'stopOffsetTranslation'.

    Operates in place and returns timespan list.


    Stretches timespans relative to timespan list start offset.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.translateOffsets(50, 50);
    l.cs;


    Translate timespan stop offsets only.

    l = FoscTimespanList([
        FoscTimespan(0, 3),
        FoscTimespan(3, 6),
        FoscTimespan(6, 10)
    ]);

    l.translateOffsets(stopOffsetTranslation: 20);
    l.cs;
    -------------------------------------------------------------------------------------------------------- */
    translateOffsets { |startOffsetTranslation, stopOffsetTranslation|
        var timespans;
        
        timespans = [];
        
        this.do { |timespan|
            timespan = timespan.translateOffsets(startOffsetTranslation, stopOffsetTranslation);
            timespans = timespans.add(timespan);
        };
        
        collection = timespans;
        
        ^this;
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
        ^throw("%:%: can't get offsets for object: %.".format(this.species, thisMethod.name, object));
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
