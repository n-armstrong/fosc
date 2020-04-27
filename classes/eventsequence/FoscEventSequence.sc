/* ------------------------------------------------------------------------------------------------------------
• FoscEventSequence

A well-formed sequence of q-events.

Contains only pitched q-events and silent q-events, and terminates with a single terminal q-event.

A q-event sequence is the primary input to the quantizer.

A q-event sequence provides a number of convenience functions to assist with instantiating new sequences.


!!!TODO
• newFromDurationsInSeconds
• newFromPitchesAndDurationsInSeconds
• newFromOffsetsInSeconds


• Example 1

Initialize from music.

a = FoscLeafMaker().(["C#4", "D4", nil], [3/8,1/8,5/16,3/16]);
b = FoscStaff(a);
y = FoscEventSequence(b);
y.inspect;


• Example 2

Initialization fails when isSimultaneous is true.

a = FoscLeafMaker().(["C#4", "D4"], [[3, 8], [1, 8], [5, 16], [3, 16]]);
b = FoscScore({ FoscStaff(a) } ! 2);
y = FoscEventSequence(b);


• Example 3

Initialize from a FoscSelection.

a = FoscLeafMaker().(["C#4", "D4"], [[3, 8], [1, 8], [5, 16], [3, 16]]);
y = FoscEventSequence(a);
y.inspect;


• Example 4

Initialize from a list of durations. 

d = [1/4, 1/4, 1/8, -1/8, -1/12, 2/12];
a = FoscEventSequence.newFromDurations(d);
a.inspect;



• Example 5

Initialize from a list of pitches and durations. 

p = "C4 E4 G4 <C5 Eb5>";
d = [1/4, 2/4, 3/16, 1/16];
a = FoscEventSequence.newFromPitchesAndDurations(p, d);
a.inspect;
a.show;


• Example 6

Initialize from a list of offsets. 

o = [0, 0.25, 0.75, 1.25, 1.5, 2];
a = FoscEventSequence.newFromOffsets(o);
a.inspect;
a.show;


• Example 7

Play the sequences accounting for tempo and dynamics. Pedal controls assigned in default instrumentMapper.

m = FoscLeafMaker().("C#4 D4", [3/8, 1/8, 5/16, 3/16]);
m = FoscStaff([FoscVoice(m, name: 'foo')], name: 'bar');
m.leafAt(0).attach(FoscMetronomeMark(1/4, 120));
FoscSelection(m).byLeaf[0..1].attach(FoscPianoPedalSpanner());
m.leafAt(0).attach(FoscDynamic('fff'));
//m.leafAt(2).attach(FoscDynamic('p'));
y = FoscEventSequence(m);

y.play;
y.pause;
y.resume;
y.stop;


• Example 7

User supplied instrumentMapper.

m = FoscLeafMaker().("C#4 D4 Eb4 F4", [3/8, 1/8, 5/16, 3/16]);
m = FoscStaff(m);
m.leafAt(0).attach(FoscMetronomeMark(1/4, 120));
a = FoscEventSequence(m);
a.instrumentMapper_(FoscMIDIPlaybackManager(midiChan: 1));
a.play;
------------------------------------------------------------------------------------------------------------ */
FoscEventSequence : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <sequence, <player;
    *newFromSequence { |sequence|
        if (sequence.isKindOf(FoscEventSequence)) { sequence = sequence.sequence };
    	^super.new.init(sequence);
    }    
    init { |argSequence|
    	var prototype, offsets, errorMsg;
    	prototype = [FoscPitchedEvent, FoscSilentEvent];
    	if (argSequence.isNil) {
    		sequence = List[];
    	} {
    		sequence = argSequence;
    		errorMsg = ("%: badly formed sequence.".format(this.species));
    		if (sequence.isEmpty) { error(errorMsg) };
            sequence.copy.drop(-1).do { |each|
    			if (prototype.includes(each.species).not) { error(errorMsg) };
    		};
    		if (sequence.last.isKindOf(FoscTerminalEvent).not) { error(errorMsg) };
    		offsets = sequence.collect { |each| each.offset };
    		offsets.doAdjacentPairs { |a, b| if (a > b) { error(errorMsg) } };
    		if (sequence[0].offset != 0) { error(errorMsg) };
    	};
        //!!!! player = FoscPlayer();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • newFromDurations

    Creates a FoscEventSequence from an array of durations. Negative durations are rests.
    
    d = [-2.5, 5, -1, -3, 1.25, -1];
	a = FoscEventSequence.newFromDurations(d, fuseSilences: true);
	a.inspect;
    a.show;

	d = [-2.5, 5, -1, -3, 1.25, -1].collect { |each| FoscDuration(each) };
	a = FoscEventSequence.newFromDurations(d, fuseSilences: false);
	a.inspect;
    a.show;

    d = [1/4, -1/4, -1/12, 1/6, 1/8, -1/8];
    a = FoscEventSequence.newFromDurations(d, fuseSilences: false);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    *newFromDurations { |durations, fuseSilences=false|
        var pitches;
        pitches = durations.collect { |each| if (each > 0) { FoscPitch(60) } { nil } };
        ^this.newFromPitchesAndDurations(pitches, durations, fuseSilences);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *newFromPitchesAndDurations (abjad: from_millisecond_pitch_pairs)

    • Example 1

    p = "C4 E4 G4 <C5 Eb5>";
    d = [1/4, 2/4, 3/16, 1/16];
    a = FoscEventSequence.newFromPitchesAndDurations(p, d);
    a.inspect;
    a.show;

    • Example 2

    p = ["C4", "E4", nil, "<C5 Eb5>"];
    d = [1/4, 2/4, 3/16, 1/16];
    a = FoscEventSequence.newFromPitchesAndDurations(p, d);
    a.inspect;
    a.show;



    a = FoscEventSequence(#[60,61], [0.125, 0.25]);
    b = FoscEventSequence(#[72,73,74], #[0.3125]);
    x = a.merge(b);
    x.play;
    x.show;
    -------------------------------------------------------------------------------------------------------- */
    *new { |pitches, durations, initialOffset, fuseSilences=false|
        var size, offsets, qEvents, pitch, offset, qEvent;
        pitches = FoscPitchParser(pitches);
        size = [pitches.size, durations.size].maxItem;
        pitches = pitches.wrapExtend(size);
        durations = durations.wrapExtend(size);
        durations = durations.asFloat;
        if (fuseSilences) {
            durations = durations.separate { |a, b| a.sign != b.sign };
            durations.collectInPlace { |each| if (each[0] < 0) { each.sum } { each } };
            durations = durations.flat;
        };
        offsets = ([0] ++ durations.abs.integrate);
        qEvents = [];
        offsets.drop(-1).do { |offset, i|
            pitch = pitches[i];
            offset = FoscOffset(offset);
            qEvent = if (pitch.isNil) { FoscSilentEvent(offset) } { FoscPitchedEvent(offset, pitch) };
            qEvents = qEvents.add(qEvent);
        };
        qEvents = qEvents.add(FoscTerminalEvent(FoscOffset(offsets.last)));
        ^super.new.init(qEvents);
    }
	/* --------------------------------------------------------------------------------------------------------
    • *newFromOffsets
    
    Creates a FoscEventSequence from an array of offsets.
    
	Returns a FoscEventSequence.

   	o = [0, 0.25, 0.75, 1.75, 3, 4];
   	a = FoscEventSequence.newFromOffsets(o);
	a.inspect;
    -------------------------------------------------------------------------------------------------------- */
    *newFromOffsets { |offsets|
    	var qEvents;
    	offsets = offsets.asFloat;
    	qEvents = offsets.drop(-1).collect { |each| FoscPitchedEvent(each, [60]) };
    	qEvents = qEvents.add(FoscTerminalEvent(offsets.last));
    	^super.new.init(qEvents);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prNewFromMusic

    !!!TODO: rework with new FoscPlaybackBundle class
    !!! update score and use effectiveIndicators (dynamic, metronomeMark, etc.) as below.
    !!! NB: some indicators do not remain effective when not bound to corrext context, e.g. compare:

    
    • FoscDynamic with FoscVoice
    a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscDynamic('p'));
    iterate(a).byLeaf.do { |leaf| leaf.prGetEffective(FoscDynamic).postln };
    
    • FoscDynamic without FoscVoice
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscDynamic('p'));
    iterate(a).byLeaf.do { |leaf| leaf.prGetEffective(FoscDynamic).postln };

    • FoscMetronomeMark without FoscScore
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscMetronomeMark(#[1,4], 60));
    iterate(a).byLeaf.do { |leaf| leaf.prGetEffective(FoscMetronomeMark).postln };

    • FoscMetronomeMark with FoscScore
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    b = FoscScore([a]);
    a[0].attach(FoscMetronomeMark(#[1,4], 60));
    iterate(a).byLeaf.do { |leaf| leaf.prGetEffective(FoscMetronomeMark).postln };


    • template
    a = FoscVoice(FoscLeafMaker().(60 ! 12, [1/4]));
    b = FoscScore([a]);
    b.leafAt(0).attach(FoscLilypondComment("sustainOn"));
    b.leafAt(0).attach(FoscMetronomeMark([1,4], 60));
    b.leafAt(6).attach(FoscMetronomeMark([1,4], 120));
    b.leafAt(0).attach(FoscDynamic('p'));    
    b.prUpdateNow(offsetsInSeconds: true, indicators: true);
    b.format;

    iterate(b).byLeaf.do { |leaf|
        c = leaf.prGetEffective(FoscLilypondComment);
        [
        leaf.prGetParentage.scoreIndex,
        leaf.prGetParentage.firstInstanceOf(FoscVoice),
        leaf.startOffsetInSeconds.asFloat,
        leaf.prGetDurationInSeconds.asFloat,
        leaf.prGetEffective(FoscDynamic).cs,
        if (c.notNil) { c.str }
        ].postln
    };


    b.prGetDurationInSeconds.asFloat;


    ### TODO: get indicator mapper from ScoreTemplate where possible

    a = FoscLeafMaker().(["C#4", "D4", nil], [3/8,1/8,5/16,3/16]);
    b = FoscStaff(a, name: 'foo');
    y = FoscEventSequence(b);
    
    a = FoscLeafMaker().(["C#4", "D4", nil], [3/8,1/8,5/16,3/16]);
    b = FoscStaff(a, name: 'foo');
    c = FoscStaff(a.deepCopy, name: 'bar');
    d = FoscStaff(a.deepCopy);
    x = FoscScore();
    x.addAll([b, c, d]);

    y = FoscEventSequence(b);
    y = FoscEventSequence(c);
    y = FoscEventSequence(d);

    FoscParentage.dumpInterface

    try { [].get(34) } { "sdf" }
    -------------------------------------------------------------------------------------------------------- */
    //!!! DEPRECATE ??
    // *prNewFromMusic { |music|
    //     //!!!TODO: assert allAreContiguousComponentsInSameLogicalVoice
    //     var offset, index, logicalVoice, effectiveDynamic, newDynamic, firstLeaf, attachments;
    //     var metronomeMark, effectiveMetronomeMark, duration, pitches, event, sequence, new;
    //     var dynamicMapper, indicatorMapper, formatStr, matchedKeys, amp, bundleActions; //#####
    //     var staff; //#####

    //     dynamicMapper = FoscDynamicMapper();        //#####
    //     indicatorMapper = FoscIndicatorMapper();    //#####
    //     offset = FoscOffset(0);
    //     effectiveDynamic = FoscDynamic('mf'); // default
    //     effectiveMetronomeMark = FoscMetronomeMark(#[1, 4], 60); // default

    //     firstLeaf = music.leafAt(0);
    //     if (firstLeaf.parent.notNil) {
    //         staff = firstLeaf.prGetParentage.firstInstanceOf(FoscStaff);
    //     };
        
    //     iterate(music).byLogicalTie.do { |logicalTie, i|
    //         firstLeaf = logicalTie[0]; 
    //         attachments = [];  
    //         metronomeMark = firstLeaf.prGetEffective(FoscMetronomeMark); //!!! SLOW!
    //         metronomeMark = metronomeMark ?? {
    //             attachments.detect { |item| item.isKindOf(FoscMetronomeMark) };
    //         };
    //         if (metronomeMark != effectiveMetronomeMark && { metronomeMark.notNil }) {
    //             effectiveMetronomeMark = metronomeMark;
    //         };
    //         duration = effectiveMetronomeMark.durationToSeconds(logicalTie.prGetDuration);
    //         amp = dynamicMapper.(firstLeaf);
    //         bundleActions = indicatorMapper.(firstLeaf);
    //         if (logicalTie.isPitched) {
    //             pitches = if (firstLeaf.isKindOf(FoscChord)) { firstLeaf.pitches } { [firstLeaf.pitch] };
    //             event = FoscPitchedEvent(offset, pitches, attachments, i, amp, bundleActions);
    //         } {
    //             event = FoscSilentEvent(offset, attachments, i, bundleActions);
    //         };
    //         offset = offset + duration;
    //         sequence = sequence.add(event);

    //         "index: ".post; event.index.postln; //!!!
    //     };

    //     sequence = sequence.add(FoscTerminalEvent(offset));
    //     ^this.new(sequence);
    //     //!!! new.logicalVoice_(logicalVoice); //!!! HACK - CHANGE
    //     //!!! ^new;
    // }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • do

    Iterates over FoscEvents in sequence.
    -------------------------------------------------------------------------------------------------------- */
    do { |func|
        ^sequence.do(func);
    }
    /* --------------------------------------------------------------------------------------------------------
    • includes

    Is true when q-event sequence contains expr. Otherwise false.
	
	Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    includes { |expr|
    	^sequence.includes(expr);
    }
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when q-event sequence equals expr. Otherwise false.
    
	Returns true or false.
    -------------------------------------------------------------------------------------------------------- */
    == { |expr|
    	var exprSequence;
    	exprSequence = expr.sequence;
    	if (this.species != expr.species) { ^false };
    	sequence.do { |each, i| if (each != exprSequence[i]) { ^false } };
    	^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats q-event sequence.
    
    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • asFoscSelection


    • Example 1

    p = "C4 E4 G4 <C5 Eb5>";
    d = [1/4, 2/4, 3/16, 1/16];
    a = FoscEventSequence.newFromPitchesAndDurations(p, d);
    b = a.asFoscSelection;
    b.play;
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    asFoscSelection {
        var pitches, durations, maker, selection;
        pitches = sequence.drop(-1).collect { |event|
            case
            { event.isKindOf(FoscPitchedEvent) } {
                if (event.pitches.size == 1) { event.pitches[0] } { event.pitches }
            }
            { event.isKindOf(FoscSilentEvent) } {
                nil;
            };
        };
        durations = this.durations;
        maker = FoscLeafMaker();
        selection = maker.(pitches, durations);
        ^selection;
    }
    /* --------------------------------------------------------------------------------------------------------
    • at
    -------------------------------------------------------------------------------------------------------- */
    at { |index|
    	^sequence[index];
    }
    /* --------------------------------------------------------------------------------------------------------
    • copySeries
    -------------------------------------------------------------------------------------------------------- */
    copySeries { |first, second, last|
        ^sequence.copySeries(first, second, last);
    }
    /* --------------------------------------------------------------------------------------------------------
    • durations
    -------------------------------------------------------------------------------------------------------- */
    durations {
        var result;
        result = [];
        this.offsets.doAdjacentPairs { |a, b|
            result = result.add(FoscDuration(b - a));
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • offsets
    -------------------------------------------------------------------------------------------------------- */
    offsets {
        ^sequence.collect { |each| each.offset };
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    Hashes q-event sequence.
    
    Required to be explicitly redefined on Python 3 if __eq__ changes.
    
    Returns integer.
    -------------------------------------------------------------------------------------------------------- */

    /* --------------------------------------------------------------------------------------------------------
    • iter
    Iterates q-event sequence.
	
	Returns a Routine.

   	d = [-2.5, 5, -1, -3, 1.25, -1];
	a = FoscEventSequence.newFromDurations(d, fuseSilences: true);
	b = a.iter;
	b.next.inspect;
    -------------------------------------------------------------------------------------------------------- */
    iter {
    	^sequence.iter;
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectByIndex
    !!! DEPRECATE ??
    -------------------------------------------------------------------------------------------------------- */
    selectByIndex { |index=0|
        ^sequence.select { |each| each.index == index };
    }
    /* --------------------------------------------------------------------------------------------------------
    • size

    !!!TODO: should this include the terminal event ??

    Size of eventSequence.
    
	Returns nonnegative Integer.

	d = [-2.5, 5, -1, -3, 1.25, -1];
	a = FoscEventSequence.newFromDurations(d, fuseSilences: true);
	a.size;
    -------------------------------------------------------------------------------------------------------- */
    size {
    	^sequence.size;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PLAYBACK
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    p = ["C4", "E4", nil, "<C5 Eb5>"];
    d = [1/4, 2/4, 3/16, 1/16];
    a = FoscEventSequence(p, d);
    a.play;
    a.pause;
    a.resume;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC  INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • merge

    Merge FoscEventSequence with another FoscEventSequence.

    Events in the merged sequence are ordered by offset.

    Returns a new FoscEventSequence.


    • Example 1

    a = FoscEventSequence(#[nil,60,61], #[0.25]);
    b = FoscEventSequence(#[73,74,75], #[0.3125]);
    x = a.merge(b);
    x.play;
    x.show;
    x.inspect;


    x = WTGO_interleaver.makeSequence(pitches: Pseq(#[71,70,69,65,64,63], repeats: 10));
    y = WTGO_interleaver.makeSequence(pitches: Pseq(#[68,67,66,62,61,60], repeats: 8));
    x = x.merge(y);

    m = MIDIOut.newByName("IAC Driver", "IAC Bus 1");
    d = x.durations.collect { |e| e.asFloat };
    p = x.pitches.collect { |e| e.pitchNumber };
    x = Pbind(\type, \midi, \midiout, m, \midinote, Pseq(p), \dur, Pseq(d), \amp, 0.5).play;

    x.inspect;
    y.inspect;
    -------------------------------------------------------------------------------------------------------- */
    merge { |eventSequence|
        var localSequence, terminalEvent;
        
        localSequence = sequence ++ eventSequence.sequence;

        terminalEvent = localSequence.select { |each| each.isKindOf(FoscTerminalEvent) };
        terminalEvent = terminalEvent.maxItem { |each| each.offset };
        localSequence = localSequence.reject { |each| each.isKindOf(FoscTerminalEvent) };

        localSequence = localSequence.sort { |a, b| a.offset <= b.offset };
        localSequence = localSequence.separate { |a, b| a.offset != b.offset };

        localSequence = localSequence.collect { |each|
            if (each.size > 1) {
                if (each.every { |event| event.isKindOf(FoscSilentEvent) }) {
                    each[0];
                } {
                    each.select { |event| event.isKindOf(FoscPitchedEvent) }.reduce('prFuse');
                };
            } {
                each
            };
        };
        localSequence = localSequence.flat;
        localSequence = localSequence.add(terminalEvent);
        ^this.species.newFromSequence(localSequence);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration
    
    Duration of the FoscEventSequence.
	
	Returns a FoscDuration.

	d = [-2.5, 5, -1, -3, 1.25, -1];
	a = FoscEventSequence.newFromDurations(d, fuseSilences: true);
	a.duration.asFloat;
    -------------------------------------------------------------------------------------------------------- */
    duration {
    	^FoscDuration(sequence.last.offset);
    }
    /* --------------------------------------------------------------------------------------------------------
    • pitches


    • Example 1

    a = FoscEventSequence.newFromPitchesAndDurations((60..72), #[0.25]);
    a.pitches.collect { |each| each.str }.postln;
    -------------------------------------------------------------------------------------------------------- */
    pitches {
        var pitches, localPitches;
        pitches = [];
        sequence.do { |each|
            if (each.isKindOf(FoscPitchedEvent)) {
                localPitches = each.pitches;
                if (localPitches.size > 1) {
                    pitches = pitches.add(each.pitches);
                } {
                    pitches = pitches.addAll(each.pitches);   
                };
            };
        };
        ^pitches;
    }
    /* --------------------------------------------------------------------------------------------------------
    • show

    !!!TODO: deprecate in favour of illustrate

    p = [60, 64, 67, [72, 75]];
    d = [1/4, 1/8, 3/16, 1/12];
    a = FoscEventSequence.newFromPitchesAndDurations(p, d);
    a.show;
	-------------------------------------------------------------------------------------------------------- */
	show {
    	var selection;
        selection = this.asFoscSelection;
        FoscStaff(selection).show;
	}
	/* --------------------------------------------------------------------------------------------------------
    • inspect
	-------------------------------------------------------------------------------------------------------- */
	inspect {
        Post << this << nl;
        sequence.do { |each| each.inspect };
	}
}
