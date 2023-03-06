/* ------------------------------------------------------------------------------------------------------------
• FoscIteration

Iteration.


• Example 1

Iterates notes.

a = FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]);
FoscIteration(a).components.do { |each| each.cs.postln };
------------------------------------------------------------------------------------------------------------ */
FoscIteration {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <client, <routine, count=0;
    *new { |client|
        var type, names;

        type = [FoscComponent, FoscSelection, SequenceableCollection];
        
        if (type.any { |type| client.isKindOf(type) }.not) {
            names = type.collect { |each| each.name }.join(", ");
            ^throw("FoscIteration:new: client must be one of: %.".format(names));
        };

        ^super.new.init(client);
    }
    init { |argClient|
        client = argClient;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • client

    Gets client.

    Returns component or selection.


    • Example 1
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    FoscIteration(a).client;


    • Example 2
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    FoscIteration(a[0..]).client;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    !!! NOT YET IMPLEMENTED
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: ITERATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • all

    a = FoscMusicMaker().(durations: [1/4], pitches: #[60,62,64,65]);
    b = FoscIteration(a).components;
    b.all;
    -------------------------------------------------------------------------------------------------------- */
    all {
        var all;
        all = routine.all;
        if (all.isNil) { ^[] } { ^all };
    }
    /* --------------------------------------------------------------------------------------------------------
    • do

    a = FoscMusicMaker().(durations: [1/4], pitches: #[60,62,64,65]);
    b = FoscIteration(a).components;
    b.do { |a, i| [i, a.cs].postln };
    -------------------------------------------------------------------------------------------------------- */
    do { |func|
        var val;
        
        this.reset;
        
        while {
            val = this.next;
            val.notNil;
        } {
            func.value(val, count);
            count = count + 1;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • doAdjacentPairs

    a = FoscMusicMaker().(durations: [1/4], pitches: #[60,62,64,65]);
    b = FoscIteration(a).components;
    b.doAdjacentPairs { |a, b, i| [i, a.cs, b.cs].postln };
    -------------------------------------------------------------------------------------------------------- */
    doAdjacentPairs { |func|
        var a, b;
        
        this.reset;
        
        while {
            if (count == 0) {
                a = this.next;
                b = this.next;
            } {
                a = b;
                b = this.next;
            };

            b.notNil;
        } {
            func.value(a, b, count);
            count = count + 1;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • next

    a = FoscMusicMaker().(durations: [1/4], pitches: #[60,62,64,65]);
    b = FoscIteration(a).components;
    b.next.cs.postln;
    -------------------------------------------------------------------------------------------------------- */
    next {
        ^routine.next;
    }
    /* --------------------------------------------------------------------------------------------------------
    • nextN

    a = FoscMusicMaker().(durations: [1/4], pitches: #[60,62,64,65]);
    b = FoscIteration(a).components;
    b.nextN(3).do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    nextN { |n|
        ^routine.nextN(n);
    }
    /* --------------------------------------------------------------------------------------------------------
    • reset
    -------------------------------------------------------------------------------------------------------- */
    reset {
        routine.reset;
        count = 0;
    }
    /* --------------------------------------------------------------------------------------------------------
    • resume
    -------------------------------------------------------------------------------------------------------- */
    resume {
        ^routine.resume;
    }
    /* --------------------------------------------------------------------------------------------------------
    • stop
    -------------------------------------------------------------------------------------------------------- */
    stop {
        ^routine.stop;
    }
    /* --------------------------------------------------------------------------------------------------------
    • value

    'value' is synonymous with 'next'.

    a = FoscMusicMaker().(durations: [1/4], pitches: #[60,62,64,65]);
    b = FoscIteration(a).components;
    b.value.cs.postln;
    -------------------------------------------------------------------------------------------------------- */
    value {
        ^routine.value;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • components

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]));
    FoscIteration(a).components.do { |each| each.cs.postln };

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]));
    FoscIteration(a).components(type: FoscNote).do { |each| each.cs.postln };

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]));
    FoscIteration(a).components(FoscNote, reverse: true).do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    components { |type, exclude, doNotIterateGraceContainers=false, graceNotes=false, reverse=false|
        var localClient, iterablePrototype, graceContainer, afterGraceContainer, embeddedRoutine;

        localClient = client;
        type = type ? FoscComponent;
        if (type.isSequenceableCollection.not) { type = [type] };
        exclude = FoscIteration.prCoerceExclude(exclude);
        assert(exclude.isSequenceableCollection);
        iterablePrototype = [FoscContainer, FoscSelection, SequenceableCollection];

        if (graceNotes && localClient.isKindOf(FoscLeaf)) {
            graceContainer = localClient.graceContainer;
            afterGraceContainer = localClient.afterGraceContainer;
        };
        
        routine = Routine {    
            if (reverse.not) {
                if (doNotIterateGraceContainers.not && graceNotes && graceContainer.notNil) {
                    embeddedRoutine = FoscIteration(graceContainer).components(
                        type,
                        doNotIterateGraceContainers: doNotIterateGraceContainers,
                        graceNotes: graceNotes,
                        reverse: reverse
                    );
                    embeddedRoutine.next.yield;
                    //if (next.notNil) { next.yield };
                };
                
                if (type.any { |type| localClient.isKindOf(type) }) {
                    if (
                        (graceNotes && FoscInspection(localClient).graceNote)
                        || { graceNotes.not && FoscInspection(localClient).graceNote.not }

                    ) {
                        if (FoscIteration.prShouldExclude(localClient, exclude).not) {
                            localClient.yield;
                        };
                    };
                };
                
                if (doNotIterateGraceContainers.not && graceNotes && afterGraceContainer.notNil) {
                    embeddedRoutine = FoscIteration(afterGraceContainer).components(
                        type,
                        exclude: exclude,
                        doNotIterateGraceContainers: doNotIterateGraceContainers,
                        graceNotes: graceNotes,
                        reverse: reverse
                    );
                    embeddedRoutine.next.yield;
                    //if (next.notNil) { next.yield };
                };
                
                if (iterablePrototype.any { |type| localClient.isKindOf(type) }) {
                    localClient.do { |each|
                        embeddedRoutine = FoscIteration(each).components(
                            type: type,
                            exclude: exclude,
                            doNotIterateGraceContainers: doNotIterateGraceContainers,
                            graceNotes: graceNotes,
                            reverse: reverse
                        );
                        //embeddedRoutine = embeddedRoutine.routine;
                        embeddedRoutine.routine.embedInStream;
                    };
                };
            } {
                if (doNotIterateGraceContainers.not && graceNotes && afterGraceContainer.notNil) {
                    embeddedRoutine = FoscIteration(afterGraceContainer).components(
                        type,
                        exclude: exclude,
                        doNotIterateGraceContainers: doNotIterateGraceContainers,
                        graceNotes: graceNotes,
                        reverse: reverse
                    );
                    embeddedRoutine.next.yield;
                    //if (next.notNil) { next.yield };
                };
                
                if (type.any { |type| localClient.isKindOf(type) }) {
                    if (
                        (graceNotes && FoscInspection(localClient).graceNote)
                        || { graceNotes.not && FoscInspection(localClient).graceNote.not }

                    ) {
                        if (FoscIteration.prShouldExclude(localClient, exclude).not) {
                            localClient.yield;
                        };
                    };
                };
                
                if (doNotIterateGraceContainers.not && graceNotes && graceContainer.notNil) {
                    embeddedRoutine = FoscIteration(graceContainer).components(
                        type,
                        doNotIterateGraceContainers: doNotIterateGraceContainers,
                        graceNotes: graceNotes,
                        reverse: reverse
                    );
                    embeddedRoutine.next.yield;
                    //if (next.notNil) { next.yield };
                };
                
                if (iterablePrototype.any { |type| localClient.isKindOf(type) }) {
                    localClient.reverseDo { |each|
                        embeddedRoutine = FoscIteration(each).components(
                            type: type,
                            exclude: exclude,
                            doNotIterateGraceContainers: doNotIterateGraceContainers,
                            graceNotes: graceNotes,
                            reverse: reverse
                        );
                        //embeddedRoutine = embeddedRoutine.routine;
                        embeddedRoutine.routine.embedInStream;
                    };
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • leaves


    • iterate all leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    FoscIteration(a).leaves.do { |each| each.cs.postln };


    • iterate pitched leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    FoscIteration(a).leaves(pitched: true).do { |each| each.cs.postln };


    • iterate non-pitched leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    FoscIteration(a).leaves(pitched: false).do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    leaves { |type, pitched, graceNotes=false|
        case 
        { pitched == true } {
            type = [FoscChord, FoscNote];
        }
        { pitched == false } {
            type = [FoscMultimeasureRest, FoscRest, FoscSkip];
        }
        {
            type = FoscLeaf;
        };
        
        ^this.components(type: type, graceNotes: graceNotes);
    }
    // leaves { |pitched, graceNotes=false|
    //     var type;

    //     case 
    //     { pitched == true } {
    //         type = [FoscChord, FoscNote];
    //     }
    //     { pitched == false } {
    //         type = [FoscMultimeasureRest, FoscRest, FoscSkip];
    //     }
    //     {
    //         type = FoscLeaf;
    //     };
        
    //     ^this.components(type: type, graceNotes: graceNotes);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • logicalTies


    !!!TODO: this is buggy with selections

    a = FoscMusicMaker(beamEachRun: true);
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,-2], pitches: "c' d' ef' f'");
    b.logicalTies(pitched: true).do { |e| e.cs.postln };
    b.show;


    • iterate all logicalTies

    b = FoscMusicMaker().(durations: #[[5,16]], mask: #[2,-1,1,1], pitches: (60..64));
    a = FoscStaff(b);
    b = FoscIteration(a).logicalTies;
    b.do { |each| each.cs.postln };
    

    • iterate pitched logicalTies
    
    a = FoscStaff(FoscMusicMaker().(durations: #[[5,16]], mask: #[2,-1,1,1], pitches: (60..64)));
    b = FoscIteration(a).logicalTies(pitched: true);
    b.do { |each| each.cs.postln };


    • iterate non-pitched logicalTies
    
    a = FoscStaff(FoscMusicMaker().(durations: #[[5,16]], mask: #[2,-1,1,1], pitches: (60..64)));
    b = FoscIteration(a).logicalTies(pitched: false);
    b.do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    logicalTies { |pitched, graceNotes=false|
        var yieldedLogicalTies, logicalTie;

        yieldedLogicalTies = Set[];

        routine = Routine {
            client.doLeaves({ |leaf|
                logicalTie = leaf.prGetLogicalTie;

                if (leaf == logicalTie.head && { yieldedLogicalTies.includes(logicalTie).not }) {
                    yieldedLogicalTies.add(logicalTie);
                    logicalTie.yield;
                };
            }, pitched: pitched, graceNotes: graceNotes);    
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • timeline

    !!!TODO: 'protoype' can not currently be FoscLogicalTie

    
    • iterate all leaves

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    m = FoscIteration(c);
    m.timeline.do { |each, i| each.attach(FoscMarkup(i, 'above')) };
    c.show;


    • iterate pitched leaves

    a = FoscStaff(FoscLeafMaker().([60,61,nil,63,nil,nil,65,66], [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    m = FoscIteration(c).timeline(pitched: true);
    m.do { |each, i| each.attach(FoscMarkup(i, 'above')) };
    c.show;


    • iterate non-pitched leaves

    a = FoscStaff(FoscLeafMaker().(#[60,61,nil,63,nil,nil,65,66], [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    m = FoscIteration(c).timeline(pitched: false);
    m.do { |each, i| each.attach(FoscMarkup(i, 'above')) };
    c.show;


    !!!TODO: logical ties not working

    • iterate logical ties

    a = FoscStaff(FoscLeafMaker().(#[60,61,nil,63,nil,nil,65,66], [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [5/16]));
    c = FoscScore([a, b]);
    m = FoscIteration(c).timeline(type: FoscLogicalTie, pitched: true);
    m.do { |each, i| each.attach(FoscMarkup(i, 'above')) };
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    timeline { |type, pitched, graceNotes=false|
        var components, indices, scoreIndex;    
        
        type = type ? FoscLeaf;
        components = this.leaves(type, pitched, graceNotes).all;
        
        components = components.reverse.sort { |a, b|
            a.postln;
            a.prGetTimespan.startOffset < b.prGetTimespan.startOffset;
        };

        routine = components.iter;
    }
    // timeline { |type, exclude, pitched, reverse=false|
    //     var components, indices, scoreIndex;    
        
    //     components = all(this.leaves(type: type, exclude: exclude, pitched: pitched));
        
    //     indices = components.collect { |each|
    //         //!!!TODO: graceNotes ??
    //         scoreIndex = each.prGetParentage(graceNotes: true).scoreIndex;
    //         [each.prGetTimespan.startOffset].addAll(scoreIndex);
    //     };
        
    //     components = components[indices.orderN];
    //     if (reverse) { components = components.reverse };
    //     routine = iter(components);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • timelineByLogicalTies

    • iterate logical ties

    a = FoscStaff(FoscLeafMaker().((60..67), [5/32]));
    b = FoscStaff(FoscLeafMaker().((60..63), [5/16]));
    c = FoscScore([a, b]);
    FoscIteration(c).timelineByLogicalTies.do { |each, i| each.head.attach(FoscMarkup(i, 'above')) };
    c.show;

    • iterate pitched logical ties

    a = FoscStaff(FoscLeafMaker().(#[60,61,nil,63,nil,nil,65], [5/32]));
    b = FoscStaff(FoscLeafMaker().((60..63), [5/16]));
    c = FoscScore([a, b]);
    d = FoscIteration(c).timelineByLogicalTies(pitched: true);
    d.do { |each, i| each.head.attach(FoscMarkup(i, 'above')) };
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    timelineByLogicalTies { |pitched, graceNotes=false|
        var logicalTies, indices, scoreIndex;    
        
        logicalTies = this.logicalTies(pitched: pitched).all;
        
        indices = logicalTies.collect { |each|
            scoreIndex = each.head.prGetParentage(graceNotes: graceNotes).scoreIndex;
            [each.head.prGetTimespan.startOffset].addAll(scoreIndex);
        };
        
        logicalTies = logicalTies[indices.orderN];
        routine = iter(logicalTies);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prCoerceExclude
    -------------------------------------------------------------------------------------------------------- */
    *prCoerceExclude { |exclude|
        if (exclude.isNil) { ^[] } { ^[exclude] };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prShouldExclude
    -------------------------------------------------------------------------------------------------------- */
    *prShouldExclude { |object, exclude|
        assert(exclude.isSequenceableCollection);
        exclude.do { |symbol|
            //!!!TODO: if (FoscInspection(object).annotation(symbol)) { ^true };
        };
        ^false;
    }
}
