/* ------------------------------------------------------------------------------------------------------------
• FoscMeterManager
------------------------------------------------------------------------------------------------------------ */
FoscMeterManager : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • offsetsAtDepth

    Gets offsets at depth in offsetInventory.

    • Example 1

    i = FoscMeter([7, 4]).depthwiseOffsetInventory;
    FoscMeterManager.getOffsetsAtDepth(0, i).do { |each| each.pair.postln };
    FoscMeterManager.getOffsetsAtDepth(1, i).do { |each| each.pair.postln };
    FoscMeterManager.getOffsetsAtDepth(2, i).do { |each| each.pair.postln };
    -------------------------------------------------------------------------------------------------------- */
    *getOffsetsAtDepth { |depth, offsetInventory|
        var newOffsets, oldOffsets, difference, half, oneQuarter, threeQuarters;
        if (depth < offsetInventory.size) { ^offsetInventory[depth] };
        while { offsetInventory.size <= depth } {
            newOffsets = [];
            oldOffsets = offsetInventory.last;
            oldOffsets.doAdjacentPairs { |first, second|
                newOffsets = newOffsets.add(first);
                difference = second - first;
                half = (first + second) / 2;
                if (FoscDuration(1, 8) < difference) {
                    newOffsets = newOffsets.add(half);
                } {
                   oneQuarter = (first + half) / 2;
                   threeQuarters = (half + second) / 2;
                   newOffsets = newOffsets.add(oneQuarter);
                   newOffsets = newOffsets.add(half);
                   newOffsets = newOffsets.add(threeQuarters); 
                };
            };
            newOffsets = newOffsets.add(oldOffsets.last);
            offsetInventory = offsetInventory.add(newOffsets);
        };
        ^offsetInventory[depth];
    }
    /* --------------------------------------------------------------------------------------------------------
    • isAcceptableLogicalTie

    Is true if logical tie is acceptable.
    -------------------------------------------------------------------------------------------------------- */
    *isAcceptableLogicalTie { |logicalTieDuration, logicalTieStartsInOffsets, logicalTieStopsInOffsets,
        maximumDotCount|
        if (logicalTieDuration.isAssignable.not) { ^false };
        if (maximumDotCount.notNil && { maximumDotCount < logicalTieDuration.dotCount }) { ^false };
        if (logicalTieStartsInOffsets.not && { logicalTieStopsInOffsets.not }) { ^false };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • isBoundaryCrossingLogicalTie

    Is true if logical tie crosses meter boundaries.
    -------------------------------------------------------------------------------------------------------- */
    *isBoundaryCrossingLogicalTie { |boundaryDepth, boundaryOffsets, logicalTieStartOffset,
        logicalTieStopOffset|
        if (boundaryDepth.isNil) { ^false };
        if (boundaryOffsets.any { |each|
            each.exclusivelyBetween(logicalTieStartOffset, logicalTieStopOffset) }.not) {
            ^false;
        };
        if (boundaryOffsets.includesEqual(logicalTieStartOffset)
            && { boundaryOffsets.includesEqual(logicalTieStopOffset) }) {
            ^false;
        };
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • iterateRewriteInputs

    Iterates topmost masked logical ties, rest groups and containers in expr, masked by expr.

    Returns a Routine.

    • Example 1

    a = FoscMeasure([2, 4], [
        FoscNote("C4", [1, 4]),
        FoscNote("D4", [1, 4])
    ]);
    b = FoscMeasure([4, 4], [
        FoscNote("D4", [3, 16]),
        FoscRest([1, 16]),
        FoscRest([3, 16]),
        FoscNote("E4", [1, 16]),
        FoscTuplet([2, 3], [FoscNote("E4", [1, 8]), FoscNote("E4", [1, 8]), FoscNote("F4", [1, 8])]),
        FoscNote("F4", [1, 4])
    ]);
    c = FoscMeasure([4, 4], [
        FoscNote("F4", [1, 8]),
        FoscNote("G4", [1, 8]),
        FoscNote("G4", [1, 4]),
        FoscNote("A4", [1, 4]),
        FoscNote("A4", [1, 8]),
        FoscNote("B4", [1, 8]),
    ]);
    d = FoscMeasure([2, 4], [
        FoscNote("B4", [1, 4]),
        FoscNote("C5", [1, 4])
    ]);
    x = FoscStaff([a, b, c, d]);
    x.select.leaves[1..2].attach(FoscTie());
    x.select.leaves[5..7].attach(FoscTie());
    x.select.leaves[8..10].attach(FoscTie());
    x.select.leaves[11..12].attach(FoscTie());
    x.select.leaves[13..14].attach(FoscTie());
    x.select.leaves[15..16].attach(FoscTie());
    x.format;
    
    FoscMeterManager.iterateRewriteInputs(x[0]).do { |each| each.music.postln }; 
    >>>
    LogicalTie([Note("c'4")])
    LogicalTie([Note("d'4")])

    FoscMeterManager.iterateRewriteInputs(x[1]).do { |each| each.music.postln };
    >>>
    LogicalTie([Note("d'8.")])
    LogicalTie([Rest('r16'), Rest('r8.')])
    LogicalTie([Note("e'16")])
    Tuplet(Multiplier(2, 3), "e'8 ~ e'8 f'8 ~")
    LogicalTie([Note("f'4")])

    FoscMeterManager.iterateRewriteInputs(x[2]).do { |each| each.music.postln };
    >>>
    LogicalTie([Note("f'8")])
    LogicalTie([Note("g'8"), Note("g'4")])
    LogicalTie([Note("a'4"), Note("a'8")])
    LogicalTie([Note("b'8")])

    FoscMeterManager.iterateRewriteInputs(x[3]).do { |each| each.music.postln };
    >>>
    LogicalTie([Note("b'4")])
    LogicalTie([Note("c''4")])

    
    a = FoscMeasure([2, 4], [FoscNote(60, [2, 4])]);
    b = FoscMeasure([4, 4], [FoscNote(60, [1, 32]), FoscNote(62, [7, 8]), FoscNote(62, [1, 16]), FoscNote(64, [1, 32])]);
    c = FoscMeasure([2, 4], [FoscNote(64, [2, 4])]);
    x = FoscStaff([a, b, c]);
    x.select.leaves[0..1].attach(FoscTie());
    x.select.leaves[2..3].attach(FoscTie());
    x.select.leaves[4..5].attach(FoscTie());
    s = x[1][0..];

    m = FoscMeterManager.iterateRewriteInputs(s).all;
    m.do { |each| each.music.collect { |elem| elem.format }.join(" ").postln };
    -------------------------------------------------------------------------------------------------------- */
    *iterateRewriteInputs { |expr|
        var thisTie, lastTie, currentLeafGroup, currentLeafGroupIsSilent;
        currentLeafGroupIsSilent = false;
        ^Routine {
            expr.do { |each|
                case
                { [FoscNote, FoscChord].any { |type| each.isKindOf(type) } } {
                    thisTie = each.prGetLogicalTie;
                    if (currentLeafGroup.isNil) {
                        currentLeafGroup = [];
                    } {
                        if (
                            currentLeafGroupIsSilent
                            || { thisTie.isNil }
                            || { lastTie != thisTie }
                        ) {
                            FoscLogicalTie(currentLeafGroup).yield;
                            currentLeafGroup = [];
                        };
                    };
                    currentLeafGroupIsSilent = false;
                    currentLeafGroup = currentLeafGroup.add(each);
                    lastTie = thisTie;
                }
                { [FoscRest, FoscSkip].any { |type| each.isKindOf(type) } } {
                    if (currentLeafGroup.isNil) {
                        currentLeafGroup = [];
                    } {
                        if (currentLeafGroupIsSilent.not) {
                            FoscLogicalTie(currentLeafGroup).yield;
                            currentLeafGroup = [];
                        };
                    };
                    currentLeafGroupIsSilent = true;
                    currentLeafGroup = currentLeafGroup.add(each);
                    lastTie = nil; 
                }
                { each.isKindOf(FoscContainer) } {
                    if (currentLeafGroup.notNil) {
                        FoscLogicalTie(currentLeafGroup).yield;
                        currentLeafGroup = nil;
                        lastTie = nil;
                    };
                    each.yield;
                }
                {
                    ^throw("%:%: undhandled component: %.".format(this.species, thisMethod.name, expr));
                };
            };
            if (currentLeafGroup.notNil) {
                FoscLogicalTie(currentLeafGroup).yield;
            };
        };
    }
}
