/* ------------------------------------------------------------------------------------------------------------
• FoscSustainMask

Sustain mask.


!!!TODO:

FoscSustainMask: 'pattern' arg may be a FoscPatternList

mutate(music).sustain(pattern, fuse: false);
mutate(music).silence(pattern);



• Example 1

Rhythm-maker.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p);
a = FoscRhythmMaker();
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;


• Example 2

Fuse contiguous leaves when 'fuse' is true.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p, fuse: true);
a = FoscRhythmMaker();
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;


• Example 3

Create a talea pattern.

p = FoscPattern(#[0,1,3], period: 5);
m = FoscSustainMask(p, fuse: true);
a = FoscRhythmMaker();
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscSustainMask : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <pattern, <fuse;
    *new { |pattern, fuse=false|
        if (pattern.isKindOf(FoscSustainMask)) { pattern = pattern.pattern };
        assert(pattern.isKindOf(FoscPattern),
            "%:new: pattern must be a FoscPattern: %".format(this.species, pattern));
        assert(fuse.isKindOf(Boolean));
        ^super.new.init(pattern, fuse);
    }
    init { |argPattern, argFuse|
        pattern = argPattern;
        fuse = argFuse;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value

    m = FoscSustainMask(FoscPattern(indices: #[0,1,4,5,17,18,19]), fuse: true);
    a = FoscRhythmMaker();
    a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
    a.show;
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

        if (fuse) {
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

        containers.do { |container|
            newSelection = mutate(container).ejectContents;
            newSelections = newSelections.add(newSelection);
        };

        ^newSelections;
    }
}

