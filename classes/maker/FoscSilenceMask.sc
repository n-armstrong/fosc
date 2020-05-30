/* ------------------------------------------------------------------------------------------------------------
• FoscSilenceMask

Replaces pitched events with rests at matching indices for 'pattern'.

If 'fuseRests' is true, fuse all contiguous rests in the same parent, and extract any rest-filled tuplets.



• Example 1

Insert silences.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(7);
m = FoscSilenceMask(p);
a = FoscRhythmMaker().(1/4 ! 4, #[[1,1,1,1,1]], masks: [m]);
f = FoscLilypondFile.rhythm(a);
f.show;


• Example 2

Insert silences and fuse resulting rests.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(7);
m = FoscSilenceMask(p, fuseRests: true);
a = FoscRhythmMaker().(1/4 ! 4, #[[1,1,1,1,1]], masks: [m]);
f = FoscLilypondFile.rhythm(a);
f.show;
------------------------------------------------------------------------------------------------------------ */
FoscSilenceMask : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <pattern;
    *new { |pattern|
        if (pattern.isKindOf(FoscSilenceMask)) { pattern = pattern.pattern };
        if (pattern.isKindOf(FoscSegmentList)) { pattern = pattern.asFoscPattern };
        assert(pattern.isKindOf(FoscPattern),
            "%:new: pattern must be a FoscPattern: %".format(this.species, pattern));
        ^super.new.init(pattern);
    }
    init { |argPattern|
        pattern = argPattern;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    // value { |selection|
    //     var logicalTies, totalLogicalTies, matchingLogicalTies, rest;

    //     logicalTies = all(FoscIteration(selection).logicalTies);
    //     totalLogicalTies = logicalTies.size;
    //     matchingLogicalTies = pattern.getMatchingItems(logicalTies);

    //     matchingLogicalTies.do { |logicalTie|
    //         if (logicalTie.head.isKindOf(FoscRest).not) {
    //             logicalTie.do { |leaf|
    //                 rest = FoscRest(leaf.writtenDuration);
    //                 if (leaf.multiplier.notNil) { rest.multiplier_(leaf.multiplier) };
    //                 mutate(leaf).replace([rest]);
    //                 rest.detach(FoscTie);
    //             };
    //         };
    //     };
    // }
    /* --------------------------------------------------------------------------------------------------------
    • !!!TODO: method for use with rhythm-makers, mutation performed directly on array of selections

    p = #[3,3,3,3,3,-1,3,3,3,3,3,-1];
    m = Threads.makeImpulseRhythm(4/8 ! 8, #[5], fusePattern: p);
    f = FoscLilypondFile.rhythm(m, stretch: 0.8);
    f.show;
    -------------------------------------------------------------------------------------------------------- */
    value { |selections|
        var newSelections, containers, rests, container, logicalTies, totalLogicalTies, matchingLogicalTies;
        var rest, localSelection, groupedRests, newSelection, tupletSpecifier;

        newSelections = [];
        containers = [];
        rests = [];

        selections.do { |selection|
            container = FoscContainer();
            container.addAll(selection);
            containers = containers.add(container);
        };

        logicalTies = all(FoscIteration(selections).logicalTies);
        totalLogicalTies = logicalTies.size;
        matchingLogicalTies = pattern.getMatchingItems(logicalTies);

        matchingLogicalTies.do { |logicalTie|
            if (logicalTie.head.isKindOf(FoscRest).not) {
                logicalTie.do { |leaf|
                    rest = FoscRest(leaf.writtenDuration);
                    if (leaf.multiplier.notNil) { rest.multiplier_(leaf.multiplier) };
                    mutate(leaf).replace([rest]);
                    rest.detach(FoscTie);
                    rests = rests.add(rest);
                };
            };
        };

        // if (fuseRests) {
        //     // group by parentage and contiguity
        //     containers.do { |each| each.prUpdateNow(offsets: true) };
        //     groupedRests = rests.separate { |a, b|
        //         a.parent != b.parent || (a.timespan.stopOffset != b.timespan.startOffset);
        //     };
        //     groupedRests.do { |each, i| FoscSelection(each).prFuseLeaves };
        // };

        containers.do { |container|
            newSelection = mutate(container).ejectContents;
            newSelections = newSelections.add(newSelection);
        };

        // if (fuseRests) {
        //     tupletSpecifier = FoscTupletSpecifier(extractTrivial: true, rewriteRestFilled: true);
        //     newSelections = tupletSpecifier.(newSelections);
        // };

        ^newSelections;
    }
}
