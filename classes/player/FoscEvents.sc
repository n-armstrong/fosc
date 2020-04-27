/* ------------------------------------------------------------------------------------------------------------
• FoscEvents

!!!TODO: send control and program changes before scheduled noteOn event, rather than delaying noteOns

Events for use in scsynth and MIDI playback.

a = FoscScoreSegment(Hakon, 'A');
m = FoscLeafMaker().((60..67), [1/8]);
m.leafAt(0).attach(FoscMetronomeMark(1/4, 60));
m.leafAt(0).attach(FoscPlaybackCommand('OR48')); 
m.leafAt(2).attach(FoscPlaybackCommand('TRI')); 
m.leafAt(4).attach(FoscPlaybackCommand('M4')); 
m.leafAt(0).attach(FoscDynamic('pppp'));
a['A'].add(m);

a.play;
------------------------------------------------------------------------------------------------------------ */
FoscEvents : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *initClass {
        /* ----------------------------------------------------------------------------------------------------
        • foscMIDIBundle
        ---------------------------------------------------------------------------------------------------- */
        Event.addEventType(\foscMIDIBundle, #{
            var freqs, sustain, midiout, latency;
            var noteOnBundle, controlMsg, programMsg, msgs;

            freqs = ~freq = ~detunedFreq.value;
            ~midinote = (freqs.cpsmidi).round(1).asInteger; //!!! ~midinote = (freqs.cpsmidi);
            ~amp = ~amp.value;
            sustain = ~sustain = ~sustain.value;
            midiout = ~midiout.value;
            latency = ~strum + ~lag;
            
            noteOnBundle = ~midiEventFunctions['noteOn'].valueEnvir;
            noteOnBundle = noteOnBundle[1].collect { |midinote|
                [noteOnBundle[0], midinote, noteOnBundle[2]];
            };

            controlMsg = ~midiEventFunctions['control'].valueEnvir;
            programMsg = ~midiEventFunctions['program'].valueEnvir;

            //!!! REMOVE
            // if (~ctlNum.notNil) { "control: ".post; controlMsg.postln };
            // if (~progNum.notNil) { "program: ".post; programMsg.postln };
            // noteOnBundle.do { |each| "noteOn: ".post; each.postln };
            // Post.nl;

            msgs = {
                if (~ctlNum.notNil) { midiout.performList('control', controlMsg) };
                if (~progNum.notNil) { midiout.performList('program', programMsg)  };
                
                // delay noteOn so control and program changes have time to take effect
                thisThread.clock.sched(0.1, {
                    noteOnBundle.do { |msgArgs| midiout.performList('noteOn', msgArgs) };
                });
            };

            if (latency == 0.0, msgs, { thisThread.clock.sched(latency, msgs) });
            
            thisThread.clock.sched(sustain + latency, {
                noteOnBundle.do { |msgArgs| midiout.performList('noteOff', msgArgs) };
            });
        });
    }
}
