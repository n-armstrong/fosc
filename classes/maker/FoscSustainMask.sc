/* ------------------------------------------------------------------------------------------------------------
• FoscSustainMask

Sustain mask.


• Example 1

Rhythm-maker.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p);
a = FoscRhythmMaker();
m = a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;


• Example 2

Leaf-maker

a = FoscLeafMaker().((60..75), [1/8]);
p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p).([a]);
FoscSelection(m).show;


• Example 3

Fuse contiguous leaves when 'fuse' is true.

a = FoscLeafMaker().((60..75), [1/8]);
p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p, fuse: true).([a]);
FoscSelection(m).show;

Rewrite meter for previous example.

m = FoscMeterSpecifier(meters: #[[4,4],[4,4]], boundaryDepth: 1).(m);
FoscSelection(m).show;


• Example 4

Fuse contiguous leaves when 'fuse' is true.

p = FoscPattern(#[0,1,4,5]) | FoscPattern.last(3);
m = FoscSustainMask(p, fuse: true);
a = FoscRhythmMaker();
m = a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
FoscSelection(m).show;

Apply tuplet specifier to previous example;

m = FoscTupletSpecifier(extractTrivial: true, rewriteRestFilled: true, rewriteSustained: true).(m); FoscSelection(m).show;


• Example 5

Create a talea pattern.

p = FoscPattern(#[0,1,3], period: 6);
m = FoscSustainMask(p, fuse: true);
a = FoscRhythmMaker();
a.(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]], masks: [m]);
a.show;


• Example 6

m = FoscSustainMask(FoscPattern(indices: #[0,1,4,5,17,18,19]), fuse: true);
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
        if (pattern.isKindOf(FoscSegmentList)) { pattern = pattern.asFoscPattern };

        assert(pattern.isKindOf(FoscPattern), thisMethod, 'pattern', pattern);
        assert(fuse.isKindOf(Boolean), thisMethod, 'fuse', fuse);
        
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
    -------------------------------------------------------------------------------------------------------- */
    value { |selections|
        var newSelections, containers, container, logicalTies, totalLogicalTies, matchingLogicalTies, indices;
        var sizes, rest, newSelection;

        newSelections = [];
        containers = [];

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
            indices = indices.add(totalLogicalTies);
            sizes = indices.intervals;
            logicalTies.clumps(sizes).do { |each| FoscSelection(each).prFuseLeaves };
        } {
            pattern.copy.invert.getMatchingItems(logicalTies).do { |logicalTie|
                if (logicalTie.head.isKindOf(FoscRest).not) {
                    logicalTie.do { |leaf|
                        rest = FoscRest(leaf.writtenDuration);
                        if (leaf.multiplier.notNil) { rest.multiplier_(leaf.multiplier) };
                        mutate(leaf).replace([rest]);
                        rest.detach(FoscTie);
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
