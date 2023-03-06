/* ---------------------------------------------------------------------------------------------------------
• FoscChord

A chord.


• Example 1

a = FoscChord("C4 E4 G4", 1/4);
a.show;


• Example 2: tweak individual noteheads 1

a = FoscChord(#[65, 70], 1/4);
a.noteHeads[1].isCautionary_(true);
a.show;


• Example 3: tweak individual noteheads 2

a = FoscChord(#[65, 70], 1/4);
tweak(a.noteHeads[1]).style = 'harmonic';
a.show;


• Example 4

Initialize from a FoscNote.

a = FoscNote(60, 1/4);
a.attach(FoscArticulation('>'));
b = FoscChord(a);
b.show;


• Example 5

Initialize from a FoscChord.

a = FoscChord(#[60,64,67], 1/4);
a.attach(FoscArticulation('>'));
b = FoscChord(a);
b.show;
--------------------------------------------------------------------------------------------------------- */
FoscChord : FoscLeaf {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <noteHeads;
    *new { |writtenPitches, writtenDuration, multiplier, tag|
        var leaf, n, areCautionary, areForced, areParenthesized, drums, noteHeads, noteHead;
        
        if (writtenPitches.isKindOf(FoscLeaf)) {
            
            leaf = writtenPitches;
            
            case 
            { leaf.isKindOf(FoscChord) } {
                writtenPitches = leaf.writtenPitches;
                writtenDuration = leaf.writtenDuration;
                multiplier = leaf.multiplier;
                areCautionary = leaf.noteHeads.items.collect { |each| each.isCautionary };
                areForced = leaf.noteHeads.items.collect { |each| each.isForced };
                areParenthesized = leaf.noteHeads.items.collect { |each| each.isParenthesized };
                ^super.new(writtenDuration, multiplier, tag).prCopyOverrideAndSetFromLeaf(leaf)
                    .initFoscChord(writtenPitches, areCautionary, areForced, areParenthesized);
            }
            { leaf.isKindOf(FoscNote) } {
                writtenPitches = [leaf.writtenPitch];
                writtenDuration = leaf.writtenDuration;
                multiplier = leaf.multiplier;
                areCautionary = [leaf.noteHead.isCautionary];
                areForced = [leaf.noteHead.isForced];
                areParenthesized = [leaf.noteHead.isParenthesized];
                ^super.new(writtenDuration, multiplier, tag).prCopyOverrideAndSetFromLeaf(leaf)
                    .initFoscChord(writtenPitches, areCautionary, areForced, areParenthesized);
            };
        };

        if (writtenPitches.isString) {
            writtenPitches = FoscPitchManager.pitchStringToPitches(writtenPitches);
        };
        
        writtenPitches = writtenPitches ?? { #[60,64,67] };
        //writtenPitches = FoscPitchParser(writtenPitches); //!!!TODO: incompatible with LilypondDrums
        writtenDuration = writtenDuration ?? { FoscDuration(1, 4) };
        n = writtenPitches.size;
        areCautionary = areCautionary ?? { Array.fill(n, false) };
        areForced = areForced ?? { Array.fill(n, false) };
        areParenthesized = areParenthesized ?? { Array.fill(n, false) };

        ^super.new(writtenDuration, multiplier, tag)
            .initFoscChord(writtenPitches, areCautionary, areForced, areParenthesized);
    }
    initFoscChord { |writtenPitches, areCautionary, areForced, areParenthesized|
        var noteHead;

        noteHeads = FoscNoteHeadList(client: this);

        writtenPitches.do { |writtenPitch, i|
            if (LilypondDrums.includes(writtenPitch).not) {
                noteHead = FoscNoteHead(
                    writtenPitch,
                    isCautionary: areCautionary[i],
                    isForced: areForced[i],
                    isParenthesized: areParenthesized[i]
                );
            } {
                noteHead = FoscDrumNoteHead(
                    writtenPitch,
                    isCautionary: areCautionary[i],
                    isForced: areForced[i],
                    isParenthesized: areParenthesized[i]
                );
            };
            noteHeads.add(noteHead);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    a = FoscChord(#[60,64,67], 1/4);
    a.cs;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        var pitches, duration;
        pitches = this.writtenPitches.items.collect { |each| each.name };
        pitches = pitches.join(" ");
        duration = this.writtenDuration.str;
        ^"FoscChord(\"%\", %)".format(pitches, duration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • storeArgs

    Gets new arguments.

    Returns array.

    a = FoscChord(#[60, 64, 67], 1/4);
    a.storeArgs;
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[this.writtenPitches, this.writtenDuration];
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prFormatBeforeSlot

    a = FoscChord([61, 64], 1/4);
    m = FoscTremolo(beamCount: 2);
    a.attach(m);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    prFormatBeforeSlot { |bundle|
        var result, commands, tremoloCommand;
        result = [];
        result = result.add(this.prFormatGraceBody);
        result = result.add(['comments', bundle.before.comments]);
        commands = bundle.before.commands;
        if (FoscInspection(this).hasIndicator(FoscTremolo)) {
            tremoloCommand = this.prFormatRepeatTremoloCommand;
            commands = commands.add(tremoloCommand);
        };
        result = result.add(['commands', commands]);
        result = result.add(['indicators', bundle.before.indicators]);
        result = result.add(['grobOverrides', bundle.grobOverrides]);
        result = result.add(['contextSettings', bundle.contextSettings]);
        result = result.add(['spanners', bundle.before.spanners]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatCloseBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatCloseBracketsSlot { |bundle|
        var result, bracketsClose;
        result = [];
        if (FoscInspection(this).hasIndicator(FoscTremolo)) {
            bracketsClose = ["}"];
            result = result.add([#['closeBrackets', ""], bracketsClose]);
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatLeafNucleus

    a = FoscChord(#[61, 64], 1/4);
    a.prFormatLeafNucleus;
    -------------------------------------------------------------------------------------------------------- */
    prFormatLeafNucleus {
        var indent, result, currentFormat, formatList;
        var reAttackDuration, durationString, duratedPitches, duratedPitch, tremolo;
        
        indent = FoscLilyPondFormatManager.indent;
        result = [];
        
        case
        { noteHeads.items.collect { |each| each.format }.any { |str| str.contains("\n") } } {
            noteHeads.do { |noteHead, i|
                currentFormat = noteHead.format;
                formatList = currentFormat.split("\\n");
                formatList = formatList.collect { |each| indent ++ each };
                result = result.addAll(formatList);
            };

            result = result.insert(0, "<");
            result = result.add(">");
            result = result.join("\n");
            result = result ++ this.prGetFormattedDuration;
        }
        { FoscInspection(this).hasIndicator(FoscTremolo) } {
            reAttackDuration = this.prTremoloReattackDuration;
            durationString = reAttackDuration.lilypondDurationString;
            duratedPitches = [];

            noteHeads.do { |noteHead|
                duratedPitch = noteHead.format ++ durationString;
                duratedPitches = duratedPitches.add(duratedPitch);
            };
            
            tremolo = FoscInspection(this).indicatorOfType(FoscTremolo);
            
            if (tremolo.isSlurred) {
                duratedPitches[0] = duratedPitches[0] ++ " \(";
                duratedPitches[duratedPitches.lastIndex] = duratedPitches.last ++ " \)";
            };
            
            result = duratedPitches.join(" ");
        }
        {
            result = result.addAll(noteHeads.items.collect { |each| each.format });
            result = "<%>%".format(result.join(" "), this.prGetFormattedDuration);
        };
        
        ^['nucleus', [result]];
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpenBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpenBracketsSlot {
        var result, bracketsOpen;
        result = [];
        if (FoscInspection(this).hasIndicator(FoscTremolo)) {
            bracketsOpen = ["{"];
            result = result.add([#['openBrackets', ""], bracketsOpen]);
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatRepeatTremoloCommand
    -------------------------------------------------------------------------------------------------------- */
    prFormatRepeatTremoloCommand {
        var tremolo, reattackDuration, repeatCount, command;
        tremolo = FoscInspection(this).indicatorOfType(FoscTremolo);
        reattackDuration = this.prTremoloReattackDuration;
        repeatCount = writtenDuration / reattackDuration / 2;
        if ((repeatCount % 1) == 0) {
            ^throw("%:%: tremolo duration % can't have % beams.".format(
                this.species, thisMethod.name, writtenDuration.pair, tremolo.beamCount));
        };
        repeatCount = repeatCount.asFloat.asInteger;
        command = "\\repeat tremolo %".format(repeatCount);
        ^command;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentation
    
    a = FoscChord("C4 E4 G4", 1/4);
    a.prGetCompactRepresentation;
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentation {
        ^("<%>%".format(this.prGetSummary, this.prGetFormattedDuration));
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetCompactRepresentationWithTie
    -------------------------------------------------------------------------------------------------------- */
    prGetCompactRepresentationWithTie {
        var logicalTie;
        logicalTie = this.prGetLogicalTie;
        if (logicalTie.size > 1 && { logicalTie.last != this }) {
            ^"% ~".format(this.prGetCompactRepresentation);
        } {
            ^this.prGetCompactRepresentation;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetSoundingPitches
    -------------------------------------------------------------------------------------------------------- */
    prGetSoundingPitches {
        ^this.notYetImplemented(thisMethod);
        // if 'sounding pitch' in inspect(self).indicators(str):
        //     return self.written_pitches
        // else:
        //     instrument = self._get_effective(instruments.Instrument)
        //     if instrument:
        //         sounding_pitch = instrument.middle_c_sounding_pitch
        //     else:
        //         sounding_pitch = abjad_pitch.NamedPitch('C4')
        //     interval = abjad_pitch.NamedPitch('C4') - sounding_pitch
        //     sounding_pitches = [
        //         interval.transpose(pitch)
        //         for pitch in self.written_pitches
        //         ]
        //     return tuple(sounding_pitches)
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetSummary

    a = FoscChord("C4 E4 G4", 1/4);
    a.prGetSummary;
    -------------------------------------------------------------------------------------------------------- */
    prGetSummary {
        ^noteHeads.items.collect { |noteHead| noteHead.str }.join(" ");
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetTremoloReattackDuration
    -------------------------------------------------------------------------------------------------------- */
    prGetTremoloReattackDuration {
        var tremolos, tremolo, exponent, denominator, reattackDuration;
        tremolos = FoscInspection(this).indicators(FoscTremolo);
        if (tremolos.isEmpty) { ^nil };
        tremolo = tremolos[0];
        exponent = 2 + tremolo.beamCount;
        denominator = (2 ** exponent).asInteger;
        reattackDuration = FoscDuration(1, denominator);
        ^reattackDuration;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • isPitched
    -------------------------------------------------------------------------------------------------------- */
    isPitched {
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • noteHeads

    Gets note-heads in chord.

    Returns note-head list.

    a = FoscChord("C4 E4 G4", 1/4);
    a.noteHeads.items.do { |each| each.writtenPitch.ps.postln }
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • noteHeads_

    Sets note-heads in chord.


    a = FoscChord("c' e' g'", [1, 4]);
    a.noteHeads.items.do { |each| each.writtenPitch.cs.postln };
    a.noteHeads_("F#4 B4 D#5 E5");
    a.noteHeads.items.do { |each| each.writtenPitch.ps.postln };
    -------------------------------------------------------------------------------------------------------- */
    noteHeads_ { |argNoteHeads|
        noteHeads = FoscNoteHeadList([], this);
        //"noteHeads: ".post; noteHeads.postln;
        //if (argNoteHeads.isSequenceableCollection) { argNoteHeads = FoscPitchParser(argNoteHeads) };
        noteHeads.addAll(argNoteHeads);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writtenDuration

    Gets written duration of chord.

    Set duration.

    Returns duration.


    a = FoscChord("C4 E4 G4", 1/4);
    a.writtenDuration.str;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • writtenDuration_

    Sets written duration of chord.
    
    a = FoscChord("C4 E4 G4", 1/4);
    a.writtenDuration.str;
    
    a.writtenDuration_(1/16);
    a.writtenDuration.str;
    -------------------------------------------------------------------------------------------------------- */
    writtenDuration_ { |duration|
        writtenDuration = FoscDuration(duration);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writtenPitches

    Gets written pitches in chord.

    Returns a FoscPitchSegment.
    -------------------------------------------------------------------------------------------------------- */
    writtenPitches {
        ^FoscPitchSegment(noteHeads.items.collect { |each| FoscPitch(each.writtenPitch) });
    }
    /* --------------------------------------------------------------------------------------------------------
    • writtenPitches_
    
    Sets written pitches in chord.
    
    a = FoscChord("C4 E4 G4", [1, 4]);
    a.writtenPitches.ps;
    
    a.writtenPitches_("F#4 B4 D#5 E5");
    a.writtenPitches.ps;
    -------------------------------------------------------------------------------------------------------- */
    writtenPitches_ { |pitches|
        this.noteHeads_(pitches);
    }
}
