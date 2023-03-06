/* ------------------------------------------------------------------------------------------------------------
• FoscMutation

Mutation agent.
------------------------------------------------------------------------------------------------------------ */
FoscMutation {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <client;
    *new { |client|
        ^super.new.init(client);
    }
    init { |argClient|
        client = argClient;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • applyMask

    Mask logical ties.


    • Example 1

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    mutate(a).applyMask(#[3,1,2,-2]);
    a.show;


    • Example 2

    Mask pattern applies only to part of selection when its sum is less than the number of logical ties.

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    mutate(a).applyMask(#[2,-1]);
    a.show;


    • Example 3

    Mask pattern repeats cyclically when 'isCyclic' is true.

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    mutate(a).applyMask(#[2,-1], isCyclic: true);
    a.show;


    • Example 4

    Mask pattern is truncated when its sum is greater than the number of logical ties.

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    mutate(a).applyMask(#[3,3,3]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    applyMask { |mask, isCyclic=false|
        var container, logicalTies, leaves;
        
        case 
        { client.isKindOf(FoscSelection) /*&& { client.areContiguousLogicalVoice }*/ } {
            client.prApplyMask(mask, isCyclic);
        }
        { client.isKindOf(FoscComponent) } {
            client.selectLeaves.prApplyMask(mask, isCyclic);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • copy
    
    Copies client.

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    a.show;

    b = mutate(a[0..1]).copy;
    a.addAll(b);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    copy {
        var selection, result;
        if (client.isKindOf(FoscSelection)) {
            selection = client;
        } {
            selection = FoscSelection(client);
        };
        result = selection.prCopy;
        if (client.isKindOf(FoscComponent)) {
            if (result.size == 1) { result = result[0] };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • ejectContents

    Ejects contents from outside-of-score container.

    Returns container contents as selection.


    • Example 1

    Eject leaves from Container.

    a = FoscContainer(FoscLeafMaker().(#[60,60,62,62], [1/4]));
    a.selectLeaves[0..1].tie;
    a.selectLeaves[2..3].tie;
    a.show;

    b = mutate(a).ejectContents;
    c = FoscStaff(b, lilypondType: 'RhythmicStaff');
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    ejectContents {
        ^client.prEjectContents;
    }
    /* --------------------------------------------------------------------------------------------------------
    • extract

    Extracts mutation client from score. Leaves children of mutation client in score.

    Returns mutation client.


    • Example 1

    Extract tuplets.

    m = FoscTuplet(#[3,2], [FoscNote(60, 1/4), FoscNote(64, 1/4)]);
    n = FoscTuplet(#[3,2], [FoscNote(62, 1/4), FoscNote(65, 1/4)]);
    a = FoscStaff([m, n]);
    a.leafAt(0).attach(FoscTimeSignature(#[3,4]));
    a.show;

    mutate(a[1]).extract;
    mutate(a[0]).extract;
    a.show;


    • Example 2

    Scales tuplet contents and then extracts tuplet.

    m = FoscTuplet(#[3,2], [FoscNote(60, 1/4), FoscNote(64, 1/4)]);
    n = FoscTuplet(#[3,2], [FoscNote(62, 1/4), FoscNote(65, 1/4)]);
    a = FoscStaff([m, n]);
    a.leafAt(0).attach(FoscTimeSignature(#[3,4]));
    a.show;

    mutate(a[1]).extract(scaleContents: true);
    mutate(a[0]).extract(scaleContents: true);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    extract { |scaleContents=false|
        ^client.prExtract(scaleContents);
    }
    /* --------------------------------------------------------------------------------------------------------
    • fuseLeaves


    • Example 1

    Fuse all leaves in a container.

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    a.show;

    mutate(a).fuseLeaves;
    a.show;


    • Example 2

    Fuse leaf selections.

    a = FoscMusicMaker();
    b = a.(1/4 ! 3, #[[1,1,1,1,1]], pitches: (60..74));
    b.show;

    m = b.selectLeaves.partitionBySizes(#[2,5,1,1]);
    m = b[3..11];
    // m.do { |sel| mutate(sel).fuseLeaves };
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    fuseLeaves {
        case 
        { client.isKindOf(FoscSelection) && { client.areContiguousLogicalVoice } } {
            client.prFuseLeaves;
        }
        { client.isKindOf(FoscComponent) } {
            client.selectLeaves.prFuseLeaves;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • fuseLeavesAndReplaceWithRests


    • Example 1

    Fuse all leaves in a container.

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    a.show;

    mutate(a).fuseLeavesAndReplaceWithRests;
    a.show;


    • Example 2

    Fuse leaf selections.

    a = FoscMusicMaker();
    b = a.(1/4 ! 3, #[[1,1,1,1,1]], pitches: (60..74));
    b.show;

    m = b[3..11];
    m.do { |sel| mutate(sel).fuseLeavesAndReplaceWithRests };
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    fuseLeavesAndReplaceWithRests {
        case 
        { client.isKindOf(FoscSelection) && { client.areContiguousLogicalVoice } } {
            client.prFuseLeavesAndReplaceWithRests;
        }
        { client.isKindOf(FoscComponent) } {
            client.selectLeaves.prFuseLeavesAndReplaceWithRests;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • replace

    Replaces mutation client (and contents of mutation client) with 'newContents'.

    Returns nil.


    • Exaxmple 1

    !!!TODO: does not copy wrappers when donors[0] is not a leaf (also broken in abjad)

    Replaces in-score tuplet (and children of tuplet) with notes.

    m = FoscTuplet(#[2,3], [FoscNote(60, 1/4), FoscNote(62, 1/4), FoscNote(64, 1/4)]);
    n = FoscTuplet(#[2,3], [FoscNote(62, 1/4), FoscNote(64, 1/4), FoscNote(65, 1/4)]);
    a = FoscStaff([m, n]);
    a.selectLeaves.slur;
    a.selectLeaves.hairpin('p < f');
    a.show;

    b = FoscLeafMaker().(#[60,62,64,65,60,62,64,65], [1/16]);
    mutate(m).replace(b, wrappers: true);
    a.show;


    • Example 2

    Copies no wrappers when 'wrappers' is false.

    a = FoscStaff(FoscLeafMaker().(#[60,65,67], [1/2,1/4,1/4]));
    a.leafAt(0).attach(FoscClef('alto'));
    a.show;

    mutate(a[0]).replace(FoscChord(#[62,64], 1/2));
    a.show;


    Set 'wrappers' to true to copy all wrappers from one leaf to another leaf (and avoid full-score update). Only works from one leaf to another leaf.

    a = FoscStaff(FoscLeafMaker().(#[60,65,67], [1/2,1/4,1/4]));
    a.show;

    a.leafAt(0).attach(FoscClef('alto'));
    mutate(a[0]).replace(FoscRest(1/2), wrappers: true);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    replace { |newContents, wrappers=false|
        var donors, donor, localWrappers, recipient, context, timeSignature, parent, start, stop;
        
        if (client.isKindOf(FoscSelection)) {
            donors = client;
        } {
            donors = FoscSelection(client);
        };

        //assert(donors.areContiguousSameParent);

        if (newContents.isKindOf(FoscSelection).not) { newContents = FoscSelection(newContents) };
        
        //assert(newContents.areContiguousSameParent);
        
        if (donors.isNil) { ^this };

        if (wrappers) {
            if (1 < donors.size || { donors[0].isKindOf(FoscLeaf).not }) {
                ^throw("%:%: set wrappers only with single leaf: %."
                    .format(this.species, thisMethod.name, donors));
            };
            
            if (1 < newContents.size || { newContents[0].isKindOf(FoscLeaf).not }) {
                ^throw("%:%: set wrappers only with single leaf: %."
                    .format(this.species, thisMethod.name, newContents));
            };
            
            donor = donors[0];
            localWrappers = donor.wrappers;
            recipient = newContents[0];
        };

        // always preserve time signatures
        donors.doLeaves { |leaf, i|
            if (FoscInspection(leaf).hasIndicator(FoscTimeSignature)) {
                timeSignature = leaf.prGetIndicator(FoscTimeSignature);
                newContents.leafAt(i).attach(timeSignature);
            };
        };

        # parent, start, stop = donors.prGetParentAndStartStopIndices;

        if (parent.isNil) {
            ^throw("%:%: can't replace component/s without a parent: %."
                .format(this.species, thisMethod.name, donors));
        };

        parent.prSetItem((start..(stop + 1)), newContents);
        
        if (localWrappers.isNil) { ^this };

        localWrappers.do { |wrapper|
            donor.wrappers.remove(wrapper);
            wrapper.instVarPut('component', recipient);
            recipient.wrappers.add(wrapper);
            context = wrapper.prFindCorrectEffectiveContext;
            if (context.notNil) { context.dependentWrappers.add(wrapper) };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • respellWithFlats

    !!!TODO: Move to pitchtools package.

    Respells named pitches in mutation client with flats.

    Returns nil.
    
    n = #["C#4", "D4", "D#4", "E4", "F4", "F#4"];
    a = FoscVoice(n.collect { |each| FoscNote(each, [1, 4]) });
    a.format;
    mutate(a).respellWithFlats;
    a.format;
    a.show;


    p = "cs' ds' e' fs'";
    m = FoscMusicMaker().(durations: 1/4 ! 4, pitches: p);
    mutate(m).respellWithFlats;
    a.show;


    m = FoscNote("cs'", 1/4);
    m.noteHead.writtenPitch.respell('flat');

    m = FoscPitchClass("cs");
    m = m.respell('flat');
    m.cs;
    -------------------------------------------------------------------------------------------------------- */
    respellWithFlats {
        // FoscIteration(client).leaves(pitched: true).do { |leaf|
        //     if (leaf.isKindOf(FoscChord)) {
        //         leaf.noteHeads.do { |noteHead| noteHead.writtenPitch.respellWithFlats };
        //     } {
        //         leaf.writtenPitch_(leaf.writtenPitch.respellWithFlats);
        //     };
        // };
        FoscIteration(client).leaves(pitched: true).do { |leaf|
            if (leaf.isKindOf(FoscChord)) {
                leaf.noteHeads.do { |noteHead| noteHead.writtenPitch.respell('flat') };
            } {
                leaf.writtenPitch_(leaf.writtenPitch.respell('flat'));
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • respellWithSharps

    !!!TODO: Move to pitchtools package.

    Respells named pitches in mutation client with sharps.

    Returns nil.
    
    n = #["Db4", "D4", "Eb4", "E4", "F4", "Gb4"];
    a = FoscVoice(n.collect { |each| FoscNote(each, [1, 4]) });
    a.format;
    mutate(a).respellWithSharps;
    a.format;

    //!!! TODO
    n = #["Db4", "D4", "Eb4", "E4", "F4", "Gb4"];
    a = FoscVoice(n.collect { |each| FoscNote(each, [1, 4]) });
    p = [1, 3];
    b = select(a).byLeaf(isPitched: true);
    b.byLeaf(isPitched: true, condition: { |leaf| p.includes(leaf.pitch.pitchClassNumber) });
    mutate(a).respellWithSharps;
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    respellWithSharps {
        FoscIteration(client).leaves(pitched: true).do { |leaf|
            if (leaf.isKindOf(FoscChord)) {
                leaf.noteHeads.do { |noteHead| noteHead.writtenPitch.respellWithSharps };
            } {
                leaf.writtenPitch_(leaf.writtenPitch.respellWithSharps);
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • rewriteMeter

    Rewrites the contents of logical ties in an expression to match meter.


    • Example 1
    
    a = FoscStaff(FoscLeafMaker().((60..67), [1/32,1/4,3/16,1/16,4/32,3/16,3/32,1/16]));
    a.show;

    mutate(a[0..]).rewriteMeter(FoscMeter(#[4,4]));
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
    
    //a.show;
    
    mutate(a[1][0..]).rewriteMeter(FoscMeter(#[4,4]));
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
    mutate(a[1][0..]).rewriteMeter(m);
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
    mutate(a[0..]).rewriteMeter(meter: t, maximumDotCount: 2);
    a.show;

    Constrain 'maximumDotCount' to 1.

    t = FoscTimeSignature(#[3,4]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/32,1/8,1/8,15/32]));
    a.leafAt(0).attach(t);
    mutate(a[0..]).rewriteMeter(meter: t, maximumDotCount: 1);
    a.show;

    Constrain 'maximumDotCount' to 0.

    t = FoscTimeSignature(#[3,4]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/32,1/8,1/8,15/32]));
    a.leafAt(0).attach(t);
    mutate(a[0..]).rewriteMeter(meter: t, maximumDotCount: 0);
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
    mutate(a[0..]).rewriteMeter(meter: t);
    a.show;

    With a 'boundaryDepth' of 1, logical ties which cross any offsets created by nodes with a depth of 1 in this Meter’s rhythm tree - 0/8, 3/8, 6/8 and 9/8 - which do not also begin and end at any of those offsets, will be split.

    t = FoscTimeSignature(#[9,8]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64], [2/4,2/4,1/8]));
    a.leafAt(0).attach(FoscTimeSignature(t));
    mutate(a[0..]).rewriteMeter(meter: t, boundaryDepth: 1);
    a.show;

    Another way of doing this is by setting 'preferredBoundaryDepth' on FoscMeter itself.

    t = FoscTimeSignature(#[9,8]);
    a = FoscStaff(FoscLeafMaker().(#[60,62,64], [2/4,2/4,1/8]));
    a.leafAt(0).attach(FoscTimeSignature(t));
    m = FoscMeter(t, preferredBoundaryDepth: 1);
    mutate(a[0..]).rewriteMeter(meter: m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    rewriteMeter { |meter, boundaryDepth, initialOffset, maximumDotCount, rewriteTuplets=true|
        var selection, result;
        selection = this.client;
        if (selection.isKindOf(FoscContainer)) { selection = FoscSelection(selection) };
        if (selection.isKindOf(FoscSelection).not) {
            ^throw("%:%: client must be a selection.".format(this.species, thisMethod.name));
        };
        result = FoscMeter.prRewriteMeter(selection, meter, boundaryDepth, initialOffset, maximumDotCount,
            rewriteTuplets);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRewriteMeter

    m = #[[2,4],[4,4],[2,4]];
    a = FoscLeafMaker().(#[60,62,64,65], [3/8,6/8,2/8,5/8]);
    a = FoscMeterSpecifier(m, attachTimeSignatures: true).([a]);
    FoscStaff(a).show;


    a = Segment(durations: 4/4 ! 2, divisions: 1 ! 16);
    a.mask = #[3,4,2,4,5];
    // a.split([1/4], isCyclic: true).do { |sel| sel.beam };
    a.show;

    b = mutate(a.currentSelection).rewriteMeters(#[2,4] ! 4);
    FoscStaff(b).show;

    a.rewriteMeters(#[2,4] ! 4);
    a.show(staffSize: 14);

    a = Segment(durations: 4/4 ! 2, divisions: 1 ! 16);
    a.mask = #[3,4,2,4,5];
    // a.split([1/4], isCyclic: true).do { |sel| sel.beam };
    a.rewriteMeters(#[2,4] ! 4);
    a.show(staffSize: 14);
    -------------------------------------------------------------------------------------------------------- */
    rewriteMeters { |meters, boundaryDepth, maximumDotCount, rewriteTuplets=true, attachTimeSignatures=true, attachBeams=false|
        var music, selections, durations, meterDuration, musicDuration, newSelections, staff, container, contents;
        var timeSignature, prevTimeSignature;

        music = this.client;
        meters = meters.collect { |each| FoscMeter(each) };
        durations = meters.collect { |each| FoscDuration(each) };
        meterDuration = durations.sum;
        musicDuration = music.duration;

        if (meterDuration != musicDuration) {
            error("%:%: duration of meters must be equal to duration of selections: meters: %, selections: %."
                .format(this.species, thisMethod.name, meterDuration.str, musicDuration.str));
            ^nil;
        };

        newSelections = [];
        staff = FoscStaff();

        selections = FoscMeter.prSplitAtMeasureBoundaries(music, meters);

        selections.do { |selection|
            container = FoscContainer(selection);
            staff.add(container);
        };

        staff.do { |container, i|
            mutate(container[0..]).rewriteMeter(
                meter: meters[i],
                boundaryDepth: boundaryDepth,
                maximumDotCount: maximumDotCount,
                rewriteTuplets: rewriteTuplets
            );
        };

        staff.do { |container|
            contents = container[0..];
            contents.do { |component| component.prSetParent(nil) };
            newSelections = newSelections.add(contents);
        };

        //!!!TODO: rebeam at boundary depth
        if (attachBeams) {
            warn("FoscMutation:rewriteMeters: 'attachBeams' argument not yet implemented");
            // newSelections.do { |selection| selection.beam };
        };

        if (attachTimeSignatures) {
            newSelections.do { |selection, i|
                timeSignature = FoscTimeSignature(meters[i]);
                if (timeSignature != prevTimeSignature) { selection.leafAt(0).attach(timeSignature) };
                prevTimeSignature = timeSignature;
            };
        };

        ^newSelections;
    }
    /* --------------------------------------------------------------------------------------------------------
    • rewritePitches

    !!!TODO: detach and re-attach indicators and spanners for any replaced leaves
    !!! see 'wrappers' flag in FoscMutation:replace


    • Example 1

    a = FoscStaff(FoscMusicMaker().(durations: [1/4], divisions: #[[1,1,1,1,1]]));
    mutate(a).rewritePitches(#[60,[60,64,67],63]);
    a.show;


    • Example 2

    a = FoscMusicMaker().(durations: [1/4], divisions: #[[1,1,1,1,1]]);
    mutate(a).rewritePitches("c' <c' e' g'> ef'");
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    rewritePitches { |pitches, isCyclic=true|     
        var selection;

        case
        { client.isKindOf(FoscContainer) } {
            selection = mutate(client).ejectContents;
            selection = this.prRewritePitches(selection, pitches, isCyclic);
            client.addAll(selection);
        }
        { client.isKindOf(FoscSelection) } {
            // if (client.areContiguousLogicalVoice.not) {
            //     ^throw("%:%: client must contain only contiguous components: %."
            //         .format(this.species, thisMethod.name, client.items));
            // };

            selection = this.prRewritePitches(client, pitches, isCyclic);
            client.instVarPut('items', selection.items);
        }
        {
            ^throw("%:%: client must be a FoscContainer or FoscSelection: %."
                .format(this.species, thisMethod.name, client));
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • scale

    Scales mutation client by multiplier.


    • Example 1

    Scales note duration by dot-generating multiplier.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
    a.show;

    mutate(a.leafAt(1)).scale(3/2);
    a.show;


    • Example 2

    Scales nontrivial logical tie duration by dot-generating multiplier.

    a = FoscStaff(FoscLeafMaker().(#[60,60,62], [1/8]));
    a[0..1].tie;
    a.show;

    mutate(a.leafAt(0).prGetLogicalTie).scale(3/2);
    a.show;


    • Example 3

    Scales container duration by dot-generating multiplier.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
    a.show;

    mutate(a).scale(3/2);
    a.show;


    • Example 4

    Scales note by tie-generating multiplier.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
    a.show;

    mutate(a.leafAt(1)).scale(5/4);
    a.show;


    • Example 5

    Scales nontrivial logical tie duration by tie-generating multiplier.

    a = FoscStaff(FoscLeafMaker().(#[60,60,62], [1/8]));
    a[0..1].tie;
    a.show;

    mutate(a.leafAt(0).prGetLogicalTie).scale(5/4);
    a.show;


    • Example 6

    Scales container duration by tie-generating multiplier.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
    a.show;

    mutate(a).scale(5/4);
    a.show;


    • Example 7 !!!TODO: NOT YET WORKING

    Scales note by tuplet-generating multiplier.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/8]));
    a.show;

    mutate(a.leafAt(1)).scale(2/3);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    scale { |multiplier|
        if (client.respondsTo('prScale')) {
            client.prScale(multiplier);
        } {
            if (client.isKindOf(FoscSelection).not) {
                ^throw("%:%: client must be a selection.".format(this.species, thisMethod.name));
            };
            
            client.do { |component| component.prScale(multiplier) }; 
        }
    }
    /* --------------------------------------------------------------------------------------------------------
    • split

    Splits component or selection by durations.

    Returns array of selections.
    

    • Example 1

    Splits leaves.

    d = [3/16, 7/32];
    a = FoscStaff(FoscLeafMaker().(#[60,64,62,65,60,64,62,65], [1/8]));
    m = a.selectLeaves;
    m.hairpin('p < f');
    mutate(m).split(d, tieSplitNotes: false);
    a.show;


    • Example 2

    Splits leaves cyclically.

    d = [3/16, 7/32];
    a = FoscStaff(FoscLeafMaker().(#[60,64,62,65,60,64,62,65], [1/8]));
    m = a.selectLeaves;
    m.hairpin('p < f');
    mutate(m).split(d, isCyclic: true, tieSplitNotes: false);
    a.show;


    • Example 3

    Splits tupletted leaves.

    t = FoscTuplet(2/3, FoscLeafMaker().(#[60,62,64], 1/4));
    a = FoscStaff([t, mutate(t).copy]);
    m = a.selectLeaves;
    m.slur;
    mutate(m).split([1/4], tieSplitNotes: false);
    a.show;


    • Example 4

    Splits leaves cyclically and tie split notes.

    a = FoscStaff(FoscLeafMaker().(#[60,62], [1]));
    m = a.selectLeaves;
    m.hairpin('p < f');
    mutate(m).split([3/4], isCyclic: true, tieSplitNotes: true);
    a.show;


    • Example 5

    Splits leaves with articulations.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.leafAt(0).attach(FoscArticulation('^'));
    a.leafAt(1).attach(FoscLaissezVibrer());
    a.leafAt(2).attach(FoscArticulation('^'));
    a.leafAt(3).attach(FoscLaissezVibrer());
    mutate(a.selectLeaves).split([1/8], isCyclic: true, tieSplitNotes: true);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    split { |durations, isCyclic=false, tieSplitNotes=true, repeatTies=false|
        var components, singleComponentInput, totalComponentDuration, totalSplitDuration, finalOffset;
        var result, shard, offsetIndex, currentShardDuration, durationsCopy, localDuration;
        var remainingComponents, nextSplitPoint, currentComponent, candidateShardDuration;
        var localSplitDuration, leftList, rightList, leafShards, currentDuration, advanceToNextOffset;
        var additionalRequiredDuration, leafSplitDurations, splitDurations, additionalDurations;

        components = client;
        singleComponentInput = false;
        
        if (components.isKindOf(FoscComponent)) {
            singleComponentInput = true;
            components = FoscSelection(components);
        };
        
        //assert(components.every { |each| each.isKindOf(FoscComponent) });
        if (components.isKindOf(FoscSelection).not) { components = FoscSelection(components) };
        durations = durations.collect { |each| FoscDuration(each) };
        
        if (durations.isEmpty) {
            if (singleComponentInput) { ^components } { ^[[], components] };
        };
        
        totalComponentDuration = components.duration;
        totalSplitDuration = durations.sum;

        case
        { isCyclic } {
            durations = durations.repeatToAbsSum(totalComponentDuration)
        }
        { totalSplitDuration < totalComponentDuration } {
            finalOffset = totalComponentDuration - durations.sum;
            durations = durations.add(finalOffset);
        }
        { totalComponentDuration < totalSplitDuration } {
            durations = durations.truncateToSum(totalComponentDuration);
        };

        durationsCopy = durations.copy;
        totalSplitDuration = durations.sum;
        assert(totalSplitDuration == totalComponentDuration);
        result = [];
        shard = [];
        offsetIndex = 0;
        currentShardDuration = FoscDuration(0);
        remainingComponents = components.items;
        advanceToNextOffset = true;

        block { |break|
            loop {
                if (advanceToNextOffset) {
                    if (durations.notEmpty) {
                        nextSplitPoint = durations.removeAt(0);
                    } {
                        break.value;
                    };
                };
                
                advanceToNextOffset = true;
                
                if (remainingComponents.notEmpty) {
                    currentComponent = remainingComponents.removeAt(0);
                } {
                    break.value;
                };
                
                localDuration = FoscInspection(currentComponent).duration;
                candidateShardDuration = currentShardDuration + localDuration;

                case
                { candidateShardDuration == nextSplitPoint } {
                    shard = shard.add(currentComponent);
                    result = result.add(shard);
                    shard = [];
                    currentShardDuration = FoscDuration(0);
                    offsetIndex = offsetIndex + 1;
                }
                { nextSplitPoint < candidateShardDuration } {
                    localSplitDuration = nextSplitPoint - currentShardDuration;
                    
                    if (currentComponent.isKindOf(FoscLeaf)) {
                        leafSplitDurations = [localSplitDuration];
                        currentDuration = FoscInspection(currentComponent).duration;
                        additionalRequiredDuration = currentDuration - localSplitDuration;
                        
                        splitDurations = durations.split(
                            [additionalRequiredDuration],
                            isCyclic: false,
                            overhang: true
                        );
                        
                        additionalDurations = splitDurations[0];
                        leafSplitDurations = leafSplitDurations.addAll(additionalDurations);
                        durations = splitDurations.last;
                        
                        leafShards = currentComponent.prSplitByDurations(
                            leafSplitDurations,
                            isCyclic: false,
                            tieSplitNotes: tieSplitNotes,
                            repeatTies: repeatTies
                        );
                        
                        shard = shard.addAll(leafShards);
                        result = result.add(shard);
                        offsetIndex = offsetIndex + additionalDurations.size;
                    } {
                        assert(currentComponent.isKindOf(FoscContainer));

                        if (currentComponent.isKindOf(FoscTuplet)) {
                            ^throw("%:split: can't split FoscTuplet'.").format(this.species);
                        };

                        # leftList, rightList = currentComponent.prSplitByDuration(
                            localSplitDuration,
                            tieSplitNotes: tieSplitNotes,
                            repeatTies: repeatTies
                        );

                        shard = shard.addAll(leftList);
                        result = result.add(shard);
                        remainingComponents.prSetItem((0..0), rightList);
                    };
                    shard = [];
                    offsetIndex = offsetIndex + 1;
                    currentShardDuration = FoscDuration(0);
                }        
                { candidateShardDuration < nextSplitPoint } {
                    shard = shard.add(currentComponent);
                    localDuration = FoscInspection(currentComponent).duration;
                    currentShardDuration = currentShardDuration + localDuration;
                    advanceToNextOffset = false;
                }
                {
                    ^throw("%:split: can not process candidate duration: %.")
                        .format(this.species, candidateShardDuration);
                };
            };
        };

        if (shard.notEmpty) { result = result.add(shard) };
        if (remainingComponents.notEmpty) { result = result.add(remainingComponents) };
        
        result = result.flat;
        result = FoscSelection(result).flat;
        result = result.partitionByDurations(durationsCopy, fill: 'exact');
        assert(result.every { |each| each.isKindOf(FoscSelection) });

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • transpose

    Transposes notes and chords in mutation client by interval.

    Returns nil.
    
    !!!TODO: Move to pitchtools package.
    
    n = #["Db4", "D4", "Eb4", "E4", "F4", "Gb4"];
    a = FoscVoice(n.collect { |each| FoscNote(each, [1, 4]) });
    a.format;
    mutate(a).transpose(5);
    a.format;
    mutate(a).transpose(-5).respellWithFlats;
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    transpose { |interval|
        var namedInterval, oldWrittenPitch, newWrittenPitch;
        //!!!TODO: namedInterval = FoscNamedInterval(interval);
        namedInterval = interval;
        FoscIteration(client).components(type: [FoscNote, FoscChord]).do { |leaf|
            if (leaf.isKindOf(FoscNote)) {
                oldWrittenPitch = leaf.writtenPitch;
                newWrittenPitch = oldWrittenPitch.transpose(namedInterval);
                leaf.writtenPitch_(newWrittenPitch);
            } {
                leaf.noteHeads.do { |each|
                    oldWrittenPitch = leaf.writtenPitch;
                    newWrittenPitch = oldWrittenPitch.transpose(namedInterval);
                    each.writtenPitch_(newWrittenPitch);
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • wrap

    Wraps mutation client in empty container.

    
    • Example 1

    Wraps in-score notes in tuplet.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,60,62,64], [1/8]));
    set(a).autoBeaming = false;
    b = a.selectLeaves.partitionBySizes(#[3,3]);
    b.do { |each|
        each.beam;
        each.slur;
    };
    a.show;

    
    • Example 2 !!!TODO: is Measure deprecated in abjad 3.0 ?

    Wraps leaves in measure.

    a = FoscVoice(FoscLeafMaker().((60..67), [1/8]));
    m = FoscMeasure(#[4,8], []);
    mutate(a[..3]).wrap(m);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    wrap { |container|
        var selection, parent, start, stop;
        
        if (container.isKindOf(FoscContainer).not || { container.size != 0 }) {
            ^throw("%:%: must be empty container: %.".format(this.species, thisMethod.name, container));
        };
        
        if (client.isKindOf(FoscComponent)) {
            selection = FoscSelection(client);
        } {
            selection = client;
        };
        
        // assert(selection.isKindOf(FoscSelection));
        
        # parent, start, stop = selection.prGetParentAndStartStopIndices;
        
        if (selection.areContiguousLogicalVoice.not) {
            ^throw("%:%: must be contiguous components in same logical voice: %."
                .format(this.species, thisMethod.name, selection));
        };
        
        container.instVarPut('components', selection.flat.items);
        container.selectLeaves.prSetParents(container);
        
        if (parent.notNil) {
            parent.components.insert(start, container);
            container.prSetParent(parent);
        };
        
        selection.do { |component|
            component.wrappers.do { |wrapper|
                wrapper.instVarPut('effectiveContext', nil);
                wrapper.prUpdateEffectiveContext;
            };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • client

    Returns client of mutation agent.

    Returns selection or component.
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prRewritePitches

    !!!TODO: detach and re-attach indicators and spanners for any replaced leaves
    !!! see 'wrappers' flag in FoscMutation:replace

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 6, pitches: "c' d' ef' <c' e' g'>");
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    prRewritePitches { |selection, pitches, wrap=false|
        var newSelections, containers, container, logicalTies, logicalTie, newLeaf, newSelection;

        if (pitches.isString) { pitches = FoscPitchManager.pitchStringToPitches(pitches) };
        container = FoscContainer(selection);
        logicalTies = selection.logicalTies(pitched: true);
        if (wrap) { pitches = pitches.wrapExtend(logicalTies.size) };

        pitches[..logicalTies.lastIndex].do { |pitch, i|
            logicalTie = logicalTies[i];
            
            logicalTie.do { |leaf|
                case
                { leaf.isKindOf(FoscNote) } {
                    if (
                        pitch.isSequenceableCollection || { pitch.isKindOf(FoscPitchSegment) }
                    ) {
                        newLeaf = FoscChord(leaf);
                        newLeaf.writtenPitches_(pitch);
                        mutate(leaf).replace([newLeaf]);
                    } {
                        leaf.writtenPitch_(pitch);
                    };
                }
                { leaf.isKindOf(FoscChord) } {
                    if (
                        pitch.isSequenceableCollection || { pitch.isKindOf(FoscPitchSegment) }
                    ) {
                        leaf.writtenPitches_(pitch); 
                    } {
                        newLeaf = FoscNote(leaf);
                        newLeaf.writtenPitch_(pitch);
                        mutate(leaf).replace([newLeaf]);
                    };
                }
            };
        };

        ^mutate(container).ejectContents;
    }
}
