/* ------------------------------------------------------------------------------------------------------------
• FoscFuseMask

!!!TODO: rename FoscSpanMask ???

Fuse mask.


• Example 1

Clip when absolute sum of sizes is greater than the number of logical ties

x = FoscMeterSpecifier(meters: #[[4,4]]);
y = FoscTupletSpecifier(extractTrivial: true, rewriteSustained: true, rewriteRestFilled: true);
a = FoscRhythmMaker(beamSpecifier: FoscBeamSpecifier(), meterSpecifier: x, tupletSpecifier: y);
n = a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [FoscFuseMask(#[17,20])]);
FoscLilypondFile.rhythm(n).show;
------------------------------------------------------------------------------------------------------------ */
FoscFuseMask : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <sizes, <isCyclic;
    *new { |sizes, isCyclic=false|
        assert(sizes.isSequenceableCollection);
        assert(isCyclic.isKindOf(Boolean));
        ^super.new.init(sizes, isCyclic);
    }
    init { |argSizes, argIsCyclic|
        sizes = argSizes;
        isCyclic = argIsCyclic;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • value
    -------------------------------------------------------------------------------------------------------- */
    value { |selections|
        var newSelections, containers, container, logicalTies, totalLogicalTies, localSizes, logicalTieGroups;
        var leaves, rest, leafGroups, fusedSelection, nextLeaf, newSelection;

        newSelections = [];
        containers = [];

        selections.do { |selection|
            container = FoscContainer();
            container.addAll(selection);
            containers = containers.add(container);
        };

        logicalTies = all(FoscIteration(selections).logicalTies);
        totalLogicalTies = logicalTies.size;
        localSizes = sizes.copy;

        if (isCyclic) {
            localSizes = localSizes.repeatToAbsSum(totalLogicalTies);    
        } {
            if (localSizes.abs.sum < totalLogicalTies) {
                localSizes = localSizes.addAll(Array.fill(totalLogicalTies - localSizes.abs.sum, 1));
            };
        };

        logicalTieGroups = logicalTies.clumps(localSizes.abs);
        
        logicalTieGroups.do { |logicalTieGroup, i|
            leaves = all(FoscIteration(logicalTieGroup).leaves);
            leaves.do { |leaf, j|
                if (localSizes[i] < 0) { 
                    rest = FoscRest(leaf.writtenDuration);
                    if (leaf.multiplier.notNil) { rest.multiplier_(leaf.multiplier) };
                    mutate(leaf).replace([rest]);
                    rest.detach(FoscTie);
                    leaves[j] = rest;
                };
            };
            leafGroups = leaves.separate { |a, b| a.parent != b.parent };
            leafGroups.do { |sel, j|
                fusedSelection = FoscSelection(sel).prFuseLeaves;
                nextLeaf = try { leafGroups[j + 1][0] };
                if (nextLeaf.notNil) { fusedSelection.last.attach(FoscTie()) };
            };
        };

        containers.do { |container|
            newSelection = mutate(container).ejectContents;
            newSelections = newSelections.add(newSelection);
        };

        ^newSelections;
    }
}
