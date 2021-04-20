/* ------------------------------------------------------------------------------------------------------------
• FoscMutation

Mutation agent.
------------------------------------------------------------------------------------------------------------ */
FoscMutation : FoscObject {
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
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • copy
    
    Copies client.

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    a.show;

    b = mutate(a[0..1]).copy;
    a.addAll(b);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
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
    // abjad 3.0
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
    // abjad 3.0
    extract { |scaleContents=false|
        ^client.prExtract(scaleContents);
    }
    /* --------------------------------------------------------------------------------------------------------
    • fuse

    Fuses mutation client.

    Returns fused mutation client.


    • Example 1

    a = FoscRhythmMaker();
    b = a.(divisions: [1/4], ratios: #[[2,1],[3,2],[4,3]]);
    a.show;

    
    • TODO: BROKEN - removing tuplet leaves from parent

    a = FoscRhythmMaker();
    b = a.(divisions: [1/4], ratios: #[1,1,1,1,1] ! 2);    
    c = FoscSelection(b).leaves.partitionBySizes(#[3,1,4,2]);
    c.items;
    c.do { |each| mutate(each).fuseLeaves };
    c.show;
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • fuseLeaves
    -------------------------------------------------------------------------------------------------------- */
    fuseLeaves {
        var container, selection, newSelection;

        case 
        { client.isKindOf(FoscSelection) && { client.areContiguousLogicalVoice } } {
            selection = client;
            ^selection.prFuseLeaves;
        }
        { client.isKindOf(FoscComponent) } {
            selection = FoscSelection(client);
            ^selection.prFuseLeaves;
        };
    }
    // fuse {
    //     var selection;
    //     case 
    //     { client.isKindOf(FoscComponent) } {
    //         selection = FoscSelection(client);
    //         ^selection.prFuse;
    //     }
    //     { client.isKindOf(FoscSelection) && client.areContiguousLogicalVoice } {
    //         selection = FoscSelection(client);
    //         ^selection.prFuse;
    //     };
    // }
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


    a = FoscRhythmMaker().([1/4], #[2,1,3] ! 4);
    FoscMeterSpecifier(#[[1,4],[2,4],[1,4]], attachTimeSignatures: true).(a);
    a = FoscSustainMask(FoscPattern.first(3) | FoscPattern.last(3)).(a);
    a = FoscTupletSpecifier(extractTrivial: true, rewriteRestFilled: true).(a);
    FoscStaff(a).show;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    replace { |newContents, wrappers=false|
        var donors, donor, localWrappers, recipient, context, timeSignature, parent, start, stop;
        
        if (client.isKindOf(FoscSelection)) {
            donors = client;
        } {
            donors = FoscSelection(client);
        };
        
        assert(donors.areContiguousSameParent);
        
        if (newContents.isKindOf(FoscSelection).not) { newContents = FoscSelection(newContents) };
        
        assert(newContents.areContiguousSameParent);
        
        if (donors.isNil) { ^this };
        
        if (wrappers) {
            if (1 < donors.size || { donors[0].isKindOf(FoscLeaf).not }) {
                throw("%:%: set wrappers only with single leaf: %."
                    .format(this.species, thisMethod.name, donors));
            };
            if (1 < newContents.size || { newContents[0].isKindOf(FoscLeaf).not }) {
                throw("%:%: set wrappers only with single leaf: %."
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
            throw("%:%: can't replace component/s without a parent: %."
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
    -------------------------------------------------------------------------------------------------------- */
    respellWithFlats {
        FoscIteration(client).leaves(pitched: true).do { |leaf|
            if (leaf.isKindOf(FoscChord)) {
                leaf.noteHeads.do { |noteHead| noteHead.writtenPitch.respellWithFlats };
            } {
                leaf.writtenPitch_(leaf.writtenPitch.respellWithFlats);
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
            throw("%:%: client must be a selection.".format(this.species, thisMethod.name));
        };
        result = FoscMeter.prRewriteMeter(selection, meter, boundaryDepth, initialOffset, maximumDotCount,
            rewriteTuplets);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • rewritePitches


    • Example 1 

    Rewrite written pitches for first three notes.

    a = FoscStaff(FoscLeafMaker().(#[60], 1/16 ! 16));
    mutate(a).rewritePitches(#[72,71,70]);
    a.show;


    • Example 2

    Rewrite written pitches using a pattern.

    a = FoscStaff(FoscLeafMaker().(#[60], 1/16 ! 16));
    mutate(a).rewritePitches(Pseq((72,71..67), inf));
    a.show;


    • Example 3

    Rewrite written pitches for first three chords.

    a = FoscStaff(FoscLeafMaker().(#[[60,64]], 1/16 ! 16));
    mutate(a).rewritePitches(#[[72,69],[71,68],[70,67]]);
    a.show;


    • Example 4

    Replaces chords with notes.

    a = FoscStaff(FoscLeafMaker().(#[[60,64]], 1/16 ! 16));
    mutate(a).rewritePitches(#[72,71,70]);
    a.show;


    • Example 5

    Replaces notes with chords.

    a = FoscStaff(FoscLeafMaker().(#[60], 1/16 ! 16));
    a.leafAt(0).attach(FoscArticulation('>'));
    mutate(a).rewritePitches(#[[72,69],[71,68],[70,67]]);
    a.show;


    • Example 6

    !!!TODO: BROKEN!

    Rewrite written pitches for first three notes in a selection.

    a = FoscLeafMaker().(#[60], 1/16 ! 16);
    mutate(a).rewritePitches(#[72,71,70]);
    a.show;


    • Example 8

    !!!TODO: selections must be added to a container for now, because of bug in FoscIteration:logicalTies.

    Rewrite written pitches for notes in an array of selections.

    a = FoscStaff(FoscRhythmMaker().(divisions: 1/4 ! 4, ratios: #[[1,1,1,1,1]]));
    mutate(a).fuseBySizes(sizes: #[-2,4], isCyclic: true);
    mutate(a).rewriteBeams(beamEachRun: true);
    mutate(a).rewritePitches(#[60,62,64]);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    rewritePitches { |writtenPitches|     
        var selections;

        case
        { client.isKindOf(FoscContainer) } {
            selections = mutate(client).ejectContents;
            selections = this.prRewritePitches([selections], writtenPitches);
            client.addAll(selections);
        }
        { client.isKindOf(FoscSelection) } {
            if (client.areContiguousLogicalVoice.not) {
                //!!! 'throw' not consistently being caught
                error("%:%: client must contain only contiguous components: %."
                    .format(this.species, thisMethod.name, client.items));
            };
            selections = this.prRewritePitches([selections], writtenPitches);
            client.instVarPut('items', selections);
        }
        {
            client.isSequenceableCollection
            && { client.every { |item| item.isKindOf(FoscSelection) } }
        } {
            // selections = FoscSelection(client).flat;
            // selections = pitchSpecifier.([selections], writtenPitches, isCyclic);
            this.prRewritePitches([selections], writtenPitches);
        }
        {
            throw("%:%: client must be a FoscContainer, a FoscSelection, or an array of FoscSelections: %."
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
                throw("%:%: client must be a selection.".format(this.species, thisMethod.name));
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
    • swap

    Swaps mutation client for empty container.

    Returns nil.
    -------------------------------------------------------------------------------------------------------- */
    swap {
        ^this.notYetImplemented(thisMethod);
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
        FoscIteration(client).components(prototype: [FoscNote, FoscChord]).do { |leaf|
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
    // abjad 3.0
    wrap { |container|
        var selection, parent, start, stop;
        if (container.isKindOf(FoscContainer).not || { container.size != 0 }) {
            throw("%:%: must be empty container: %.".format(this.species, thisMethod.name, container));
        };
        if (client.isKindOf(FoscComponent)) {
            selection = FoscSelection(client);
        } {
            selection = client;
        };
        assert(selection.isKindOf(FoscSelection));
        # parent, start, stop = selection.prGetParentAndStartStopIndices;
        if (selection.areContiguousLogicalVoice.not) {
            throw("%:%: must be contiguous components in same logical voice: %."
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
    -------------------------------------------------------------------------------------------------------- */
    prRewritePitches { |selections, writtenPitches|
        var n, newSelections, containers, pitches, container, logicalTies, totalLogicalTies;
        var pitchStream, pitch, logicalTie, newLeaf, newSelection;

        newSelections = [];
        containers = [];
        pitches = [];

        selections.do { |selection|
            container = FoscContainer();
            container.addAll(selection);
            containers = containers.add(container);
        };

        logicalTies = all(FoscIteration(selections).logicalTies(pitched: true));
        totalLogicalTies = logicalTies.size;

        if (writtenPitches.isKindOf(Pattern)) {
            pitchStream = writtenPitches.asStream;
            totalLogicalTies.do {
                block { |break|
                    pitch = pitchStream.next;
                    if (pitch.isNil) { break.value };
                    pitches = pitches.add(pitch);
                };
            };
        } {
            pitches = writtenPitches;
        };

        pitches = FoscPitchParser(pitches);

        // • TODO: detach and re-attach indicators for any replaced leaves

        block { |break|
            pitches.do { |writtenPitch, i|
                logicalTie = logicalTies[i];
                logicalTie.do { |leaf|
                    case
                    { leaf.isKindOf(FoscNote) } {
                        if (
                            writtenPitch.isSequenceableCollection
                            || { writtenPitch.isKindOf(FoscPitchSegment) }
                        ) {
                            newLeaf = FoscChord(leaf);
                            newLeaf.writtenPitches_(writtenPitch);
                            mutate(leaf).replace([newLeaf]);
                        } {
                            leaf.writtenPitch_(writtenPitch);
                        };
                    }
                    { leaf.isKindOf(FoscChord) } {
                        if (
                            writtenPitch.isSequenceableCollection
                            || { writtenPitch.isKindOf(FoscPitchSegment) }
                        ) {
                            leaf.writtenPitches_(writtenPitch); 
                        } {
                            newLeaf = FoscNote(leaf);
                            newLeaf.writtenPitch_(writtenPitch);
                            mutate(leaf).replace([newLeaf]);
                        };
                    }
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
