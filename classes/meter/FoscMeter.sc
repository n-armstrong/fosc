/* ------------------------------------------------------------------------------------------------------------
• FoscMeter

Meter models a common practice understanding of beats and other levels of rhythmic organization structured as a tree. Meter structure corresponds to the monotonically increasing sequence of factors in the numerator of a given time signature. Successively deeper levels of the tree divide time by successive factors.

• Example 1

FoscMeter(FoscTimeSignature(#[5,4])).inspect;
FoscMeter(FoscDuration(5, 4)).inspect;
FoscMeter(FoscFraction(5, 4)).inspect;
FoscMeter(#[5,4]).inspect;
FoscMeter(FoscRhythm(#[5,4], #[2,3])).inspect;
FoscMeter(FoscRhythm(#[5,4], #[1,[7,[3,2,2]], 2])).inspect;
FoscMeter(#[5,4], increaseMonotonic: true).inspect;


a = FoscMeter([5,4]);
a.inspect;
a.increaseMonotonic;
a.denominator;
a.numerator;
a.preferredBoundaryDepth;
a.rootNode;

FoscMeter([5/4,[2,3]])
------------------------------------------------------------------------------------------------------------ */
FoscMeter : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <increaseMonotonic, <denominator, <numerator, <preferredBoundaryDepth, <rootNode;
    *new { |object, increaseMonotonic=false, preferredBoundaryDepth|
        ^super.new.init(object, increaseMonotonic, preferredBoundaryDepth);
    }
    init { |object, argIncreaseMonotonic, argPreferredBoundaryDepth|
        var fraction, factors;
        object = object ?? { #[4,4] };
        increaseMonotonic = argIncreaseMonotonic;
        preferredBoundaryDepth = argPreferredBoundaryDepth;
        case 
        { object.isKindOf(this.species) } {
            rootNode = object.rootNode;
            # numerator, denominator = object.pair;
            increaseMonotonic = object.increaseMonotonic;
            preferredBoundaryDepth = object.preferredBoundaryDepth;
        }
        { object.isSequenceableCollection } {
            # numerator, denominator = object;
            //fraction = FoscNonreducedFraction(numerator, denominator);
            rootNode = FoscRhythm([numerator, denominator]);
            factors = this.prFactors(numerator);
            this.prRecurse(rootNode, factors, denominator, increaseMonotonic);
        }
        { object.isFloat } {
            ^this.species.new(object.asFraction);
        }
        { object.isInteger } {
            ^this.species.new([object, 1]);
        }
        { object.isKindOf(FoscRhythm) } {
            object.do { |node|
                if (node.prolation.denominator.isPowerOfTwo.not) {
                    ^throw("%:%: can't contain tuplets.".format(this.species, thisMethod.name));
                };
            };
            rootNode = object;
            # numerator, denominator = rootNode.prGetPreprolatedDuration.pair;
        }
        { [FoscFraction, FoscTimeSignature].any { |type| object.isKindOf(type) } } {
            # numerator, denominator = object.pair;
            rootNode = FoscRhythm([numerator, denominator]);
            factors = this.prFactors(numerator);
            this.prRecurse(rootNode, factors, denominator, increaseMonotonic);
        }
        { ^throw("%:%: can't initialize from %.".format(this.species, thisMethod.name, object.species)) };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when argument is a meter with an rtm format equal to that of this meter. Otherwise false.

    Returns true or false.
    

    !!!TODO: not yet implemented
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats meter.

    Returns string.

    • Example 1

    Gets storage format of 7/4:


    a = FoscMeter(#[7,4]);
    a.format;

    >>> print(format(meter))
    abjad.Meter(
        '(7/4 ((3/4 (1/4 1/4 1/4)) (2/4 (1/4 1/4)) (2/4 (1/4 1/4))))'
        )
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    Hashes meter.

    Returns integer.
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • inspect
    
    • Example 1

    a = FoscMeter(#[7,4]);
    a.inspect;

    • Example 2

    a = FoscMeter(#[7,4], increaseMonotonic: true);
    a.inspect;
    -------------------------------------------------------------------------------------------------------- */
    inspect {
        this.rootNode.do { |each|
            each.depth.do { Post.tab };
            Post << each.duration.str << nl;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • iter

    Iterates meter.

    Yields pairs.
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • str

    !!!TODO: make identical to abjad.

    Gets string representation of meter.

    Returns string.
    
    
    • Example 1

    FoscMeter([7, 4]).str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^"%/%".format(this.numerator, this.denominator);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prFactors
    -------------------------------------------------------------------------------------------------------- */
    prFactors { |numerator|
        var factors;
        factors = numerator.factors;
        if (factors.size > 1 && { factors[0] == 2 } && { factors[1] == 2 }) {
            factors = factors.drop(2);
            factors = [4] ++ factors;
        };
        ^factors;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRecurse
    -------------------------------------------------------------------------------------------------------- */
    prRecurse { |node, factors, denominator, increaseMonotonic=false|
        var factor, preProlatedDuration, child, parts, total, grouping;
        if (factors.notEmpty) {
            factor = factors[0];
            factors = factors[1..];
            preProlatedDuration = node.prGetPreprolatedDuration / factor;
            if (factor.inclusivelyBetween(2, 4)) {
                if (factors.notEmpty) {
                    factor.do {
                        child = FoscRhythm(preProlatedDuration);
                        node.add(child);
                        this.prRecurse(child, factors, denominator, increaseMonotonic);
                    };
                } {
                    factor.do { node.add(FoscRhythmLeaf([1, denominator])) };
                };
            } {
                parts = [3];
                total = 3;
                while { total < factor } {
                    if (increaseMonotonic.not) {
                        parts = parts.add(2);
                    } {
                        parts = parts.insert(0, 2);
                    };
                    total = total + 2;
                };
                parts.do { |part|
                    grouping = FoscRhythm(preProlatedDuration * part);
                    if (factors.notEmpty) {
                        part.do {
                            child = FoscRhythm(preProlatedDuration);
                            grouping.add(child);
                            this.prRecurse(child, factors, denominator, increaseMonotonic);
                        };
                    } {
                        part.do { grouping.add(FoscRhythmLeaf([1, denominator])) };
                    };
                    node.add(grouping);
                };
            };
        } {
            node.prGetPreprolatedDuration.numerator.do { node.add(FoscRhythmLeaf([1, denominator])) };
        };
        ^node;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prRewriteMeter


    • Example 1
    
    a = FoscStaff(FoscLeafMaker().((60..67), [1/32,1/4,3/16,1/16,4/32,3/16,3/32,1/16]));
    FoscMeter.prRewriteMeter(a[0..], FoscMeter(#[4,4]));
    a.show;


    • Example 2

    FoscContainer used to specify measure boundaries.
    
    a = FoscStaff([
        FoscContainer([FoscNote(60, 2/4)]),
        FoscContainer([FoscLeafMaker().([60,62,62,64], [1/32,7/8,1/16,1/32])]),
        FoscContainer([FoscNote(64, 2/4)])
    ]);

    a.leafAt(0).attach(FoscTimeSignature(#[2,4]));
    a.leafAt(1).attach(FoscTimeSignature(#[4,4]));
    a.leafAt(5).attach(FoscTimeSignature(#[2,4]));
    
    m = a.selectLeaves;
    m[0..1].tie;
    m[2..3].tie;
    m[4..5].tie;
    
    a.show;
    
    FoscMeter.prRewriteMeter(a[1][0..], FoscMeter(#[4,4]));
    a.show;


    • Example 3

    Use FoscRhythm to specify custom metrical hierarchy.


    a = FoscStaff([
        FoscContainer([FoscNote(60, 2/4)]),
        FoscContainer([FoscLeafMaker().([60,62,62,64], [1/32,7/8,1/16,1/32])]),
        FoscContainer([FoscNote(64, 2/4)])
    ]);
    a.leafAt(0).attach(FoscTimeSignature(#[2,4]));
    a.leafAt(1).attach(FoscTimeSignature(#[4,4]));
    a.leafAt(5).attach(FoscTimeSignature(#[2,4]));
    m = a.selectLeaves;
    m[0..1].tie;
    m[2..3].tie;
    m[4..5].tie;
  
    m = FoscRhythm(4/4, #[[2,[1,1]],[2,[1,1]]]);
    FoscMeter.prRewriteMeter(a[1][0..], m);
    a.show;


    • Example 4

    Limit the maximum number of dots per leaf using 'maximumDotCount'.

    No constraint.

    t = FoscTimeSignature(#[3,4]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/32,1/8,1/8,15/32]));
    a.leafAt(0).attach(t);
    a.show;

    Constrain 'maximumDotCount' to 2.

    t = FoscTimeSignature(#[3,4]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/32,1/8,1/8,15/32]));
    a.leafAt(0).attach(t);
    FoscMeter.prRewriteMeter(a[0..], meter: t, maximumDotCount: 2);
    a.show;

    Constrain 'maximumDotCount' to 1.

    t = FoscTimeSignature(#[3,4]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/32,1/8,1/8,15/32]));
    a.leafAt(0).attach(t);
    FoscMeter.prRewriteMeter(a[0..], meter: t, maximumDotCount: 1);
    a.show;

    Constrain 'maximumDotCount' to 0.

    t = FoscTimeSignature(#[3,4]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/32,1/8,1/8,15/32]));
    a.leafAt(0).attach(t);
    FoscMeter.prRewriteMeter(a[0..], meter: t, maximumDotCount: 0);
    a.show;


    • Example 5

    Split logical ties at different depths of the Meter, if those logical ties cross any offsets at that depth, but do not also both begin and end at any of those offsets.

    t = FoscTimeSignature(#[9,8]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64], [2/4,2/4,1/8]));
    a.leafAt(0).attach(FoscTimeSignature(t));
    a.show;

    Establish meter without specifying 'boundaryDepth'.

    t = FoscTimeSignature(#[9,8]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64], [2/4,2/4,1/8]));
    a.leafAt(0).attach(FoscTimeSignature(t));
    FoscMeter.prRewriteMeter(a[0..], meter: t);
    a.show;

    With a 'boundaryDepth' of 1, logical ties which cross any offsets created by nodes with a depth of 1 in this Meter’s rhythm tree - 0/8, 3/8, 6/8 and 9/8 - which do not also begin and end at any of those offsets, will be split.

    t = FoscTimeSignature(#[9,8]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64], [2/4,2/4,1/8]));
    a.leafAt(0).attach(FoscTimeSignature(t));
    FoscMeter.prRewriteMeter(a[0..], meter: t, boundaryDepth: 1);
    a.show;

    Another way of doing this is by setting the 'preferredBoundaryDepth' on the FoscMeter itself.

    t = FoscTimeSignature(#[9,8]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64], [2/4,2/4,1/8]));
    a.leafAt(0).attach(FoscTimeSignature(t));
    m = FoscMeter(t, preferredBoundaryDepth: 1);
    FoscMeter.prRewriteMeter(a[0..], meter: m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    *prRewriteMeter { |components, meter, boundaryDepth, initialOffset, maximumDotCount, rewriteTuplets=false|
        var recurse, firstStartOffset, lastStartOffset, difference, firstOffset, prolation, offsetInventory;
        var boundaryOffsets, items, preProlatedDuration, subMetricalHierarchy, subBoundaryDepth, denominator;
        var offsets, logicalTieDuration, logicalTieTimespan, logicalTieStartOffset, logicalTieStopOffset;
        var logicalTieStartsInOffsets, logicalTieStopsInOffsets, splitOffset, logicalTies, shards;

        if (components.isKindOf(FoscSelection).not) {
            ^throw("%:%: components must be a FoscSelection: %."
                .format(this.species, thisMethod.name, components));
        };

        meter = FoscMeter(meter);
        boundaryDepth = boundaryDepth ?? { meter.preferredBoundaryDepth };
        
        recurse = { |boundaryDepth, boundaryOffsets, depth=0, logicalTie|
            offsets = FoscMeterManager.getOffsetsAtDepth(depth, offsetInventory);
            logicalTieDuration = logicalTie.prGetPreprolatedDuration;
            logicalTieTimespan = logicalTie.timespan;
            logicalTieStartOffset = logicalTieTimespan.startOffset;
            logicalTieStopOffset = logicalTieTimespan.stopOffset;
            logicalTieStartsInOffsets = offsets.includesEqual(logicalTieStartOffset);
            logicalTieStopsInOffsets = offsets.includesEqual(logicalTieStopOffset);

            case
            {
                FoscMeterManager.isAcceptableLogicalTie(
                    logicalTieDuration: logicalTieDuration,
                    logicalTieStartsInOffsets: logicalTieStartsInOffsets,
                    logicalTieStopsInOffsets: logicalTieStopsInOffsets,
                    maximumDotCount: maximumDotCount
                ).not;
            } {
                splitOffset = nil;
                offsets = FoscMeterManager.getOffsetsAtDepth(depth, offsetInventory);
                if (logicalTieStartsInOffsets) { offsets = offsets.reverse };
                block { |break|
                    offsets.do { |offset|
                        if (logicalTieStartOffset < offset && { offset < logicalTieStopOffset }) {
                            splitOffset = offset;
                            break.value;
                        };
                    };
                };
                if (splitOffset.notNil) {
                    splitOffset = splitOffset - logicalTieStartOffset;
                    shards = mutate(logicalTie[0..]).split([splitOffset]);
                    logicalTies = [];
                    shards.do { |selection| logicalTies = logicalTies.add(FoscLogicalTie(selection.items)) };
                    logicalTies.do { |logicalTie|
                        recurse.(boundaryDepth, boundaryOffsets, depth, logicalTie);
                    };
                } {
                    recurse.(boundaryDepth, boundaryOffsets, depth + 1, logicalTie);
                };
            }
            {
                FoscMeterManager.isBoundaryCrossingLogicalTie(
                    boundaryDepth: boundaryDepth,
                    boundaryOffsets: boundaryOffsets,
                    logicalTieStartOffset: logicalTieStartOffset,
                    logicalTieStopOffset: logicalTieStopOffset
                );
            } {
                offsets = boundaryOffsets;
                if (boundaryOffsets.includesEqual(logicalTieStartOffset)) {
                    offsets = boundaryOffsets.reverse;
                };
                splitOffset = nil;
                block { |break|
                    offsets.do { |offset|
                        if (logicalTieStartOffset < offset && { offset < logicalTieStopOffset }) {
                            splitOffset = offset;
                            break.value;
                        };
                    };
                };
                assert(splitOffset.notNil);
                splitOffset = splitOffset - logicalTieStartOffset;
                shards = mutate(logicalTie[0..]).split([splitOffset]);
                logicalTies = [];
                shards.do { |selection|
                    logicalTies = logicalTies.add(FoscLogicalTie(selection.items));
                };
                logicalTies.do { |logicalTie|
                    recurse.(boundaryDepth, boundaryOffsets, depth, logicalTie);
                };
            }
            {
                logicalTie[0..].prFuse;
            };
        };

        assert(components.isKindOf(FoscSelection));
        if (meter.isKindOf(FoscMeter).not) { meter = FoscMeter(meter) };
        boundaryDepth = boundaryDepth ?? { meter.preferredBoundaryDepth };
        assert(FoscSelection(components).areContiguousLogicalVoice);
        if (boundaryDepth.notNil) { boundaryDepth = boundaryDepth.asInteger };
        if (maximumDotCount.notNil) { maximumDotCount = maximumDotCount.asInteger };
        if (initialOffset.isNil) { initialOffset = FoscOffset(0) };
        initialOffset = FoscOffset(initialOffset);
        firstStartOffset = FoscInspection(components[0]).timespan.startOffset;
        lastStartOffset = FoscInspection(components.last).timespan.startOffset;
        difference = lastStartOffset - firstStartOffset + initialOffset;
        if (difference >= meter.impliedTimeSignature.duration) {
            ^throw("%:%: offset difference is greater than duration of implied time signature: %."
                .format(this.species, thisMethod.name, difference.str));
        };
        firstOffset = FoscInspection(components[0]).timespan.startOffset;
        firstOffset = firstOffset - initialOffset;
        if (components[0].parent.isNil) {
            prolation = 1;
        } {
            prolation = components[0].parent.prGetParentage.prolation;
        };
        offsetInventory = [];
        meter.depthwiseOffsetInventory.do { |offsets|
            offsets = offsets.collect { |each| (each * prolation) + firstOffset };
            offsetInventory = offsetInventory.add(offsets);
        };
        if (boundaryDepth.notNil) { boundaryOffsets = offsetInventory[boundaryDepth] };

        items = all(FoscMeterManager.iterateRewriteInputs(components));
        items.do { |item, i|
            case 
            { item.isKindOf(FoscLogicalTie) } {
                recurse.(boundaryDepth, boundaryOffsets, 0, item);
            }
            { item.isKindOf(FoscTuplet) && { rewriteTuplets.not } } {
                // pass
            }
            {
                preProlatedDuration = FoscDuration(0);
                item.do { |each| preProlatedDuration = preProlatedDuration + each.prGetPreprolatedDuration };
                if (preProlatedDuration.numerator == 1) {
                    preProlatedDuration = FoscNonreducedFraction(preProlatedDuration);
                    denominator = preProlatedDuration.denominator * 4;
                    preProlatedDuration = preProlatedDuration.withDenominator(denominator);
                };
                subMetricalHierarchy = FoscMeter(preProlatedDuration);
                subBoundaryDepth = 1;
                if (boundaryDepth.isNil) { subBoundaryDepth = nil };
                FoscMeter.prRewriteMeter(
                    item[0..],
                    subMetricalHierarchy,
                    boundaryDepth: subBoundaryDepth,
                    maximumDotCount: maximumDotCount
                );
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prSplitAtMeasureBoundaries
  
    
    • Example 1

    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    //a.show;
    b = FoscMeterSpecifier.prSplitAtMeasureBoundaries([a], #[[4,4],[4,4]]);
    FoscStaff(b).show;


    • Example 2

    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    //a.show;
    b = FoscMeterSpecifier.prSplitAtMeasureBoundaries(a, #[[2,4],[2,4],[2,4],[2,4]]);
    FoscStaff(b).show;

    FoscMutation

    l = Layer(divisions: 1!10, subdivisions: #[[1,1,1,1,1],[1,1,1]], tempo: 72);
    l.applySegmentation(#[9,11,11,9]);
    l.applyMasks(#[[-1,2,3]]);
    l.asFoscComponent;
    l.format;
    l.show;
    -------------------------------------------------------------------------------------------------------- */
    *prSplitAtMeasureBoundaries { |selections, meters|
        var durations, container, components, componentDurations, partSizes;
        
        durations = meters.collect { |each| FoscDuration(each) };
        container = FoscContainer(selections);
        mutate(container[0..]).split(durations: durations, tieSplitNotes: true);
        components = mutate(container).ejectContents;
        componentDurations = components.items.collect { |each| each.prGetDuration };
        partSizes = componentDurations.split(durations).collect { |each| each.size };
        selections = components.partitionBySizes(partSizes).items;
        
        ^selections;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • fitMeters
    
    Finds the best-matching sequence of meters for the offsets contained in argument.

    Coerces offsets from argument via MetricAccentKernel.count_offsets().

    Coerces Meters from meters via MeterList.

    Returns array.


    • Example XXX


    >>> meters = [(3, 4), (4, 4), (5, 4)]
    >>> meters = [abjad.Meter(_) for _ in meters]

    • Example XXX

    Matches a series of hypothetical 4/4 measures:


        >>> argument = [(0, 4), (4, 4), (8, 4), (12, 4), (16, 4)]
        >>> for x in abjad.Meter.fit_meters(
        ...     argument, meters):
        ...     print(x.implied_time_signature)
        ...
        4/4
        4/4
        4/4
        4/4

    • Example XXX

    Matches a series of hypothetical 5/4 measures:


        >>> argument = [(0, 4), (3, 4), (5, 4), (10, 4), (15, 4), (20, 4)]
        >>> for x in abjad.Meter.fit_meters(
        ...     argument, meters):
        ...     print(x.implied_time_signature)
        ...
        3/4
        4/4
        3/4
        5/4
        5/4

    
    @staticmethod
    def fit_meters(
        argument,
        meters,
        denominator=32,
        discard_final_orphan_downbeat=True,
        maximum_run_length=None,
        starting_offset=None,
        ):
        from abjad.tools import metertools
        session = metertools.MeterFittingSession(
            kernel_denominator=denominator,
            maximum_run_length=maximum_run_length,
            meters=meters,
            offset_counter=argument,
            )
        meters = session()
        return meters
    -------------------------------------------------------------------------------------------------------- */
    *fitMeters {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • generateOffsetKernelToDenominator
    
    Generates a dictionary of all offsets in a meter up to denominator.

    Keys are the offsets and the values are the normalized weights of those offsets.

    This is useful for testing how strongly a collection of offsets responds to a given meter.

    Returns dictionary.


    • Example XXX

    m = FoscMeter((4, 4))
        >>> kernel = meter.generate_offset_kernel_to_denominator(8)
        >>> for offset, weight in sorted(kernel.kernel.items()):
        ...     print('{!s}\t{!s}'.format(offset, weight))
        ...
        0       3/16
        1/8     1/16
        1/4     1/8
        3/8     1/16
        1/2     1/8
        5/8     1/16
        3/4     1/8
        7/8     1/16
        1       3/16


    def generate_offset_kernel_to_denominator(
        self,
        denominator,
        normalize=True,
        ):
        from abjad.tools import metertools
        assert mathtools.is_positive_integer_power_of_two(
            denominator // self.denominator)

        inventory = list(self.depthwise_offset_inventory)
        old_flag_count = durationtools.Duration(1, self.denominator).flag_count
        new_flag_count = durationtools.Duration(1, denominator).flag_count
        extra_depth = new_flag_count - old_flag_count
        for _ in range(extra_depth):
            old_offsets = inventory[-1]
            new_offsets = []
            for first, second in datastructuretools.Sequence(old_offsets).nwise():
                new_offsets.append(first)
                new_offsets.append((first + second) / 2)
            new_offsets.append(old_offsets[-1])
            inventory.append(tuple(new_offsets))

        total = 0
        kernel = {}
        for offsets in inventory:
            for offset in offsets:
                if offset not in kernel:
                    kernel[offset] = 0
                kernel[offset] += 1
                total += 1

        if normalize:
            for offset, response in kernel.items():
                kernel[offset] = durationtools.Multiplier(response, total)

        return metertools.MetricAccentKernel(kernel)
    -------------------------------------------------------------------------------------------------------- */
    generateOffsetKernelToDenominator {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • decreaseDurationsMonotonically
   
    Is true when meter divides large primes into collections of 2 and 3 that decrease monotonically. Otherwise false.
    
    FoscMeter([7, 8], decreaseDurationsMonotonically: true).inspect;
    FoscMeter([7, 8], decreaseDurationsMonotonically: false).inspect;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • denominator
    
    Gets denominator of meter.
    
    FoscMeter([7, 8]).denominator;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • depthwiseOffsetInventory

    Gets depthwise offset inventory of meter.

    Returns array.

    • Example 1
    
    a = FoscMeter([7, 4]);
    a.depthwiseOffsetInventory.do { |offsets, i|
        Post << i << nl;
        offsets.do { |each| Post.space << each.pair << nl };
    };

    @property
    def depthwise_offset_inventory(self):
        inventory = []
        all_offsets = set()
        all_offsets.add(durationtools.Offset(self.numerator, self.denominator))
        for depth, nodes in sorted(self.root_node.depthwise_inventory.items()):
            for node in nodes:
                all_offsets.add(durationtools.Offset(node.start_offset))
            inventory.append(tuple(sorted(all_offsets)))
        return tuple(inventory)
    -------------------------------------------------------------------------------------------------------- */
    depthwiseOffsetInventory {
        var inventory, allOffsets, rootNodeInventory, keys, nodes;
        inventory = [];
        allOffsets = Set[];
        allOffsets.add(FoscOffset(this.numerator, this.denominator));
        rootNodeInventory = this.rootNode.depthwiseInventory;
        keys = rootNodeInventory.keys.as(Array).sort;
        keys.do { |depth|
            nodes = rootNodeInventory[depth];
            nodes.do { |node| allOffsets.add(FoscOffset(node.startOffset)) };
            inventory = inventory.add(allOffsets.as(Array).sort);
        };
        ^inventory;
    }
    /* --------------------------------------------------------------------------------------------------------
    • duration

    Gets duration of meter.

    m = FoscMeter([7, 4]);
    m.duration.inspect;
    -------------------------------------------------------------------------------------------------------- */
    duration {
        ^FoscDuration(numerator, denominator);
    }
    /* --------------------------------------------------------------------------------------------------------
    • impliedTimeSignature
    
    Gets implied time signature of meter.

    m = FoscMeter([4, 4]);
    m.impliedTimeSignature.inspect;
    -------------------------------------------------------------------------------------------------------- */
    impliedTimeSignature {
        ^FoscTimeSignature(this.pair);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isCompound
    
    Is true when meter is compound.

    Compound meters are defined as those equal with a numerator divisible by 3 but not equal 3.
    
    FoscMeter([3, 4]).isCompound;
    FoscMeter([4, 8]).isCompound;
    FoscMeter([6, 8]).isCompound;
    FoscMeter([15, 32]).isCompound;
    -------------------------------------------------------------------------------------------------------- */
    isCompound {
        if (numerator == 3) { ^false };
        ^(numerator.gcd(3) == 3);
    }
    /* --------------------------------------------------------------------------------------------------------
    • isSimple
    
    Is true when meter is simple. Otherwise false.
    
    Simple meters are defined as those equal with a numerator not divisible by 3.
    
    Meters with numerator equal to 3 are also defined as simple.
    
    FoscMeter([3, 4]).isSimple;
    FoscMeter([4, 8]).isSimple;
    FoscMeter([6, 8]).isSimple;
    FoscMeter([15, 32]).isSimple;
    -------------------------------------------------------------------------------------------------------- */
    isSimple {
        ^this.isCompound.not;
    }
    /* --------------------------------------------------------------------------------------------------------
    • numerator

    Gets numerator of meter.

    Returns positive integer.


    • Example 1
    m = FoscMeter([7, 4]);
    m.numerator;

    @property
    def numerator(self):
        return self._numerator
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • pair
    
    Gets numerator and denominator pair.
    
    FoscMeter([7, 8]).pair;
    -------------------------------------------------------------------------------------------------------- */
    pair {
        ^[numerator, denominator];
    }
    /* --------------------------------------------------------------------------------------------------------
    • preferredBoundaryDepth

    Gets preferred boundary depth of meter. Used by rewriteMeter method.

    Returns integer or nil.
    

    • Example 1
    m = FoscMeter([4, 4]);
    m.preferredBoundaryDepth;

    • Example 2
    m = FoscMeter([4, 4], preferredBoundaryDepth: 2);
    m.preferredBoundaryDepth;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rootNode

    Gets root node of meter.

    Returns rhythm tree node.
    

    • Example 1

    m = FoscMeter([7, 4]);
    m.rootNode;
    
    @property
    def root_node(self):
        return self._root_node
    -------------------------------------------------------------------------------------------------------- */
}
