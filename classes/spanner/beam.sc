/* ------------------------------------------------------------------------------------------------------------
• beam


• Example 1

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
set(a).autoBeaming = false;
a[0..1].beam;
a[2..3].beam;
a.show;


• Example 2

a = FoscStaff(FoscLeafMaker().((60..75), [1/32]));
set(a).autoBeaming = false;
a[0..].beam(durations: 1/8 ! 4, spanBeamCount: 1);
a.show;


• Example 3

Partition selection by sizes and beam each new selection.

a = FoscStaff(FoscLeafMaker().((60..75), [1/32]));
set(a).autoBeaming = false;
a[0..].partitionBySizes(#[3,4,6,3]).do { |sel| sel.beam };
a.show;


• Example 4

Beams can be tweaked.

a = FoscStaff(FoscLeafMaker().((60..75), [1/32]));
set(a).autoBeaming = false;
a[0..].partitionBySizes(#[3,4,6,3]).do { |selection|
    b = FoscStartBeam(direction: 'up', tweaks:#['positions', [6,6], 'color', 'grey']);
    selection.beam(startBeam: b);
};
a.show;


• Example 5

Specify spanning beams using 'durations' and 'spanBeamCount'.

x = FoscLeafMaker().((60..83), [1/16]);
d = [[1/4, 1/8],[1/8, 1/4],[1/4, 1/8],[1/8, 1/4]];
m = x.partitionBySizes(#[6,6,6,6]);
m.do { |sel, i| sel.beam(durations: d[i], spanBeamCount: 1) };
x.show;
------------------------------------------------------------------------------------------------------------ */
+ FoscSelection {
    beam { |startBeam, stopBeam, beamLoneNotes=false, beamRests=true, durations, spanBeamCount, stemletLength, tweaks|
        var originalLeaves, silentPrototype, isBeamable, leaves, runs, run, thisIndex, thatIndex;
        var selection, selections, localRuns, startLeaf, stopLeaf, staff, lilypondType, string, literal;
        var leafNeighbors, previousLeaf, previous, nextLeaf, next, leafDurations, parts, partCounts;
        var totalParts, isFirstPart, isLastPart, firstLeaf, flagCount, left, right, lastLeaf, beamCount;
        
        originalLeaves = this.leaves;

        originalLeaves.do { |leaf|
            leaf.detach(FoscStartBeam);
            leaf.detach(FoscStopBeam);
        };


        //!!!TODO: originalLeaves = this.leaves(doNotIterateGraceContainers: true);
        silentPrototype = [FoscMultimeasureRest, FoscRest, FoscSkip];

        leafNeighbors = { |leaf, originalLeaves|
            assert(leaf != originalLeaves.first);
            assert(leaf != originalLeaves.last);
            thisIndex = originalLeaves.indexOf(leaf);
            previousLeaf = originalLeaves[thisIndex - 1];
            previous = 0;
            
            if (FoscStartBeam.prIsBeamable(previousLeaf, beamRests: beamRests)) {
                previous = previousLeaf.writtenDuration.flagCount;
            };
            
            nextLeaf = originalLeaves[thisIndex + 1];
            next = 0;
            
            if (FoscStartBeam.prIsBeamable(nextLeaf, beamRests: beamRests)) {
                next = nextLeaf.writtenDuration.flagCount;
            };
            
            [previous, next];
        };

        leaves = [];
        
        originalLeaves.do { |leaf|
            if (FoscStartBeam.prIsBeamable(leaf, beamRests: beamRests)) { leaves = leaves.add(leaf) };
        };

        runs = [];
        run = [];
        if (leaves[0].notNil) { run = run.add(leaves[0]) };

        leaves[1..].do { |leaf|
            thisIndex = originalLeaves.indexOf(run.last);
            thatIndex = originalLeaves.indexOf(leaf);
            
            if (thisIndex + 1 == thatIndex) {
                run = run.add(leaf);
            } {
                selection = FoscSelection(run);
                runs = runs.add(selection);
                run = [leaf];
            };
        };

        if (run.notEmpty) {
            selection = FoscSelection(run);
            runs = runs.add(selection);
        };

        if (beamLoneNotes.not) { runs = runs.select { |each| each.size > 1 } };

        runs.do { |run|
            if (run.every { |each| silentPrototype.includes(each.species) }.not) {
                startLeaf = run.first;
                stopLeaf = run.last;
                startBeam = startBeam ?? { FoscStartBeam(tweaks: tweaks) };
                stopBeam = stopBeam ?? { FoscStopBeam() };
                startLeaf.detach(FoscStartBeam);
                startLeaf.attach(startBeam);
                stopLeaf.detach(FoscStopBeam);
                stopLeaf.attach(stopBeam);

                if (stemletLength.notNil) {
                    staff = startLeaf.prGetParentage.firstInstanceOf(FoscStaff);
                    lilypondType = try { staff.lilypondType } { 'Staff' };
                    string = "\\override %.Stem.stemlet-length = %".format(lilypondType, stemletLength);
                    literal = FoscLilyPondLiteral(string);
                    
                    block { |break|
                        startLeaf.prGetIndicators.do { |indicator|
                            if (indicator == literal) { break.value };
                        };
                        
                        startLeaf.attach(literal);
                    };
                    
                    staff = stopLeaf.prGetParentage.firstInstanceOf(FoscStaff);
                    lilypondType = try { staff.lilypondType } { 'Staff' };
                    string = "\\revert %.Stem.stemlet-length".format(lilypondType);
                    literal = FoscLilyPondLiteral(string, formatSlot: 'after');
                    
                    block { |break|
                        stopLeaf.prGetIndicators.do { |indicator|
                            if (indicator == literal) { break.value };
                        };
                        
                        stopLeaf.attach(literal);
                    };
                };
            };
        };

        if (durations.isNil) { ^this };
        if (originalLeaves.size == 1) { ^this };

        spanBeamCount = spanBeamCount ? 1;
        leafDurations = originalLeaves.items.collect { |each| each.prGetDuration };
        //!!!TODO: parts = leafDurations.partitionBySums(durations, overhang: true);
        //!!!TODO: ^throw error if durations.sum > leafDurations.sum ???
        durations = durations.collect { |each| FoscDuration(each) };
        durations = durations.truncateToAbsSum(leafDurations.sum);
        parts = leafDurations.split(durations, overhang: true);
        partCounts = parts.collect { |each| each.size };
        parts = originalLeaves.clumps(partCounts);
        totalParts = parts.size;

        parts.do { |part, i|
            isFirstPart = (i == 0);
            isLastPart = false;
            if (i == (totalParts - 1)) { isLastPart = true };
            firstLeaf = part[0];
            lastLeaf = part.last;
            flagCount = firstLeaf.writtenDuration.flagCount;

            if (part.size == 1) {
                if (FoscStartBeam.prIsBeamable(firstLeaf, beamRests: false)) {
                    left = if (isFirstPart) { 0 } { flagCount };
                    beamCount = FoscBeamCount(left, flagCount);
                    firstLeaf.attach(beamCount);
                };

                if (FoscStartBeam.prIsBeamable(lastLeaf, beamRests: false)) {
                    flagCount = lastLeaf.writtenDuration.flagCount;
                    right = if (isLastPart) { 0 } { flagCount };
                    beamCount = FoscBeamCount(flagCount, right);
                    lastLeaf.attach(beamCount);
                };
            } {
                if (FoscStartBeam.prIsBeamable(firstLeaf, beamRests: false)) {
                    left = if (isFirstPart) { 0 } { spanBeamCount };
                    beamCount = FoscBeamCount(left, flagCount);
                    firstLeaf.attach(beamCount);
                };

                if (FoscStartBeam.prIsBeamable(lastLeaf, beamRests: false)) {
                    flagCount = lastLeaf.writtenDuration.flagCount;
                    
                    if (isLastPart) {
                        left = flagCount;
                        right = 0;
                    } {
                        # previous, next = leafNeighbors.(lastLeaf, originalLeaves);
                        
                        case
                        { previous == next && { next == 0 }} {
                            left = flagCount;
                            right = flagCount;
                        }
                        { previous == 0 } {
                            left = 0;
                            right = flagCount;
                        }
                        { next == 0 } {
                            left = flagCount;
                            right = 0;
                        }
                        { previous == flagCount } {
                            left = flagCount;
                            right = min(spanBeamCount, next);
                        }
                        { flagCount == next } {
                            left = min(previous, flagCount);
                            right = flagCount;
                        }
                        {
                            left = flagCount;
                            right = min(previous, flagCount);
                        };
                    };
                    
                    beamCount = FoscBeamCount(left, right);
                    lastLeaf.attach(beamCount);
                };
    
                if (part.size > 2) {
                    part[1..(part.lastIndex - 1)].do { |middleLeaf|
                        
                        if (
                            FoscStartBeam.prIsBeamable(middleLeaf, beamRests: beamRests)
                            && { silentPrototype.includes(middleLeaf.species).not }
                        ) {
                            flagCount = middleLeaf.writtenDuration.flagCount;
                            # previous, next = leafNeighbors.(middleLeaf, originalLeaves);

                            case
                            { previous == next && { next == 0 } } {
                                left = flagCount;
                                right = flagCount;
                            }
                            { previous == 0 } {
                                left = 0;
                                right = flagCount;
                            }
                            { next == 0 } {
                                left = flagCount;
                                right = 0;
                            }
                            { previous == flagCount } {
                                left = flagCount;
                                right = min(flagCount, next);
                            }
                            { flagCount == next } {
                                left = min(previous, flagCount);
                                right = flagCount;
                            }
                            {
                                left = min(previous, flagCount);
                                right = flagCount;
                            };

                            beamCount = FoscBeamCount(left, right);
                            middleLeaf.attach(beamCount);
                        };
                    };
                };
            };
        };
    }
}
