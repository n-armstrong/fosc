/* ------------------------------------------------------------------------------------------------------------
• FoscSustainMask

Sustain mask.


!!!TODO:

FoscSustainMask: 'pattern' arg may be a FoscPatternList

mutate(music).sustain(pattern, hold: false, fuseRests: true);
mutate(music).silence(pattern, fuseRests: true);



• Example 1

Rhythm-maker.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p);
a = FoscRhythmMaker();
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;


• Example 2

Fuse contiguous leaves when 'hold' is true.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p, hold: true);
a = FoscRhythmMaker();
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;


• Example 3

Create a talea pattern.

p = FoscPattern(#[0,1,3], period: 5);
m = FoscSustainMask(p, hold: true);
a = FoscRhythmMaker();
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscSustainMask : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <pattern, <hold, <fuseRests;
    *new { |pattern, hold=false, fuseRests=true|
        if (pattern.isKindOf(FoscSustainMask)) { pattern = pattern.pattern };
        assert(pattern.isKindOf(FoscPattern),
            "%:new: pattern must be a FoscPattern: %".format(this.species, pattern));
        assert(hold.isKindOf(Boolean));
        assert(fuseRests.isKindOf(Boolean));
        ^super.new.init(pattern, hold, fuseRests);
    }
    init { |argPattern, argHold, argFuseRests|
        pattern = argPattern;
        hold = argHold;
        fuseRests = argFuseRests;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |selections|
        var newSelections, containers, rests, container, logicalTies, totalLogicalTies, matchingLogicalTies;
        var localSizes, logicalTieGroups, logicalTieGroup, indices, logicalTieGroupSizes;
        var leaves, rest, leavesGroupedByParent, fusedSelection, nextLeaf, newSelection;

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

        if (hold) {
            indices = matchingLogicalTies.collect { |each| logicalTies.indexOf(each) };
            if (indices.includes(0).not) { indices = [0] ++ indices };
            if (indices.includes(totalLogicalTies).not) { indices = indices ++ [totalLogicalTies] };
            logicalTieGroupSizes = indices.intervals;
            logicalTieGroups = logicalTies.clumps(logicalTieGroupSizes);    
            logicalTieGroups.do { |logicalTieGroup, i|
                leaves = all(FoscIteration(logicalTieGroup).leaves);
                // leaves.do { |leaf, j|
                //     if (localSizes[i] < 0) { 
                //         rest = FoscRest(leaf.writtenDuration);
                //         if (leaf.multiplier.notNil) { rest.multiplier_(leaf.multiplier) };
                //         mutate(leaf).replace([rest]);
                //         rest.detach(FoscTie);
                //         leaves[j] = rest;
                //     };
                // };
                leavesGroupedByParent = leaves.separate { |a, b| a.parent != b.parent };
                leavesGroupedByParent.do { |sel, j|
                    fusedSelection = FoscSelection(sel).prFuseLeaves;
                    nextLeaf = try { leavesGroupedByParent[j + 1][0] };
                    if (nextLeaf.notNil) { fusedSelection.last.attach(FoscTie()) };
                };
            };
        } {
            pattern.copy.invert.getMatchingItems(logicalTies).do { |logicalTie|
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

        };

        if (fuseRests) {
            // group by parentage and contiguity
            containers.do { |each| each.prUpdateNow(offsets: true) };
            rests.separate { |a, b|
                a.parent != b.parent || (a.timespan.stopOffset != b.timespan.startOffset);
            }.do { |each, i| FoscSelection(each).prFuseLeaves };
        };

        containers.do { |container|
            newSelection = mutate(container).ejectContents;
            newSelections = newSelections.add(newSelection);
        };

        ^newSelections;
    }
}

