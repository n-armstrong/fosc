/* ------------------------------------------------------------------------------------------------------------
• FoscIterationAgent

Iteration agent.
------------------------------------------------------------------------------------------------------------ */
FoscIterationAgent : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <client;
    var count=0, routine;
    *new { |client|
        ^super.new.init(client);
    }
    init { |argClient|
        client = argClient;
        routine = Routine { client.prIterateTopDown.do { |item| item.yield } }; // default routine
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: basic Routine interface
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • all
    -------------------------------------------------------------------------------------------------------- */
    all {
        var all;
        all = routine.all;
        if (all.isNil) { ^[] } { ^all };
    }
    /* --------------------------------------------------------------------------------------------------------
    • do
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

    a = FoscRhythm([3, 4], [1, 2, -2, 1, -1]).render;
    x = FoscIterationAgent(a).byLeaf;
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
    -------------------------------------------------------------------------------------------------------- */
    next {
        ^routine.next;
    }
    /* --------------------------------------------------------------------------------------------------------
    • nextN
    -------------------------------------------------------------------------------------------------------- */
    nextN { |n|
        ^routine.next(n);
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
    -------------------------------------------------------------------------------------------------------- */
    value {
        ^routine.value;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • client
    
    Gets client of iteration agent.

    Returns component or selection.
    
    a = FoscRhythm([3, 4], [1, 2, -2, 1, -1]);
    x = FoscIterationAgent(a);
    x.client;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • byClass

    Iterates by class.

    • iterate by FoscNote
    
    a = FoscVoice({ |i| FoscNote(60 + i, [1, 4]) } ! 4);
    b = iterate(a).byClass(FoscNote);
    b.do { |each| each.inspect };

    • iterate by FoscVoice and FoscNote
    
    a = FoscVoice({ |i| FoscNote(60 + i, [1, 4]) } ! 4);
    b = iterate(a).byClass([FoscVoice, FoscNote]);
    b.do { |each| each.inspect };

    • iterate by FoscNote, returning only notes with pitchNumber > 61
    
    a = FoscVoice({ |i| FoscNote(60 + i, [1, 4]) } ! 4);
    b = iterate(a).byClass(FoscNote, { |note| note.pitch > 61 });
    b.do { |each| each.inspect };

    • iterate over an Array
    x = { |i| FoscNote(60 + i, [1, 4]) } ! 4;
    FoscIterationAgent(x).byClass(FoscNote).do { |each| each.postln };

    • iterate over a FoscSelection
    x = { |i| FoscNote(60 + i, [1, 4]) } ! 4;
    FoscIterationAgent(FoscSelection(x)).byClass(FoscNote).do { |each| each.postln };

    • iterate over a FoscContainer
    x = { |i| FoscNote(60 + i, [1, 4]) } ! 4;
    FoscIterationAgent(FoscVoice(x)).byClass(FoscNote).do { |each| each.cs.postln };    
    -------------------------------------------------------------------------------------------------------- */
    byClass { |prototype, condition=true|
        prototype = prototype ? [FoscComponent];
        if (prototype.isSequenceableCollection.not) { prototype = [prototype] };
        routine = Routine {
            client.prIterateTopDown.do { |component|
                if (prototype.any { |type| component.isKindOf(type) }) {
                    if (condition.value(component)) { component.yield };
                };
            };  
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • byLeaf
    
    Iterates by leaf.

    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLeaf;
    x.do { |each| each.cs.postln };

    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLeaf({ |leaf| leaf.isPitched });
    x.do { |each| each.cs.postln };

    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLeaf({ |leaf| leaf.isPitched.not });
    x.do { |each| each.cs.postln };

    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLeaf(condition: { |leaf| leaf.writtenDuration >= (3/4) });
    x.do { |each| each.cs.postln };

    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLeaf { |leaf| leaf.isPitched && { leaf.pitch.pitchNumber == 60 } };
    x.do { |each| each.cs.postln };
    -------------------------------------------------------------------------------------------------------- */
    byLeaf { |condition=true|
        this.byClass([FoscLeaf], condition);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byLeafPair

    Iterates by leaf pair.

    def by_leaf_pair(self):
        import abjad
        if self._expression:
            return self._update_expression(inspect.currentframe())
        vertical_moments = self.by_vertical_moment()
        def _closure(vertical_moments):
            for moment_1, moment_2 in abjad.Sequence(vertical_moments).nwise():
                enumeration = sequencetools.Enumeration(moment_1.start_leaves)
                for pair in enumeration.yield_pairs():
                    yield abjad.select(pair)
                sequences = [moment_1.leaves, moment_2.start_leaves]
                enumeration = sequencetools.Enumeration(sequences)
                for pair in enumeration.yield_outer_product():
                    yield abjad.select(pair)
            else:
                enumeration = sequencetools.Enumeration(moment_2.start_leaves)
                for pair in enumeration.yield_pairs():
                    yield abjad.select(pair)
        return _closure(vertical_moments)
    -------------------------------------------------------------------------------------------------------- */
    byLeafPair {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byLogicalTie

    a = FoscVoice(FoscLeafMaker().(60 ! 12, [1/4]));
    b = FoscScore([a]);
    m = iterate(b).byLogicalTie.do { |m| m.postln }
    -------------------------------------------------------------------------------------------------------- */
    byLogicalTie { |exclude, graceNotes=false, nontrivial, isPitched=false|
        var yieldedLogicalTies, logicalTie;

        yieldedLogicalTies = Set[];

        ^Routine {
            //!!!TODO: this.byLeaf(exclude, graceNotes, nontrivial, isPitched).do { |leaf|
            this.byLeaf(/* !!!TODO: args */).do { |leaf|
                logicalTie = leaf.prGetLogicalTie;
                if (leaf == logicalTie.head) {
                    if (
                        nontrivial.isNil
                        || { nontrivial && { logicalTie.isTrivial.not } }
                        || { nontrivial.not && { logicalTie.isTrivial } }
                    ) {
                        if (yieldedLogicalTies.includes(logicalTie).not) {
                            yieldedLogicalTies.add(logicalTie);
                            logicalTie.yield;
                        };
                    };
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • byLogicalTie

    FoscLogicalTie

    Iterates by logical tie.


    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLogicalTie;
    x.do { |each| each.cs.postln };

    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLogicalTie({ |logicalTie| logicalTie.isPitched });
    x.do { |each| each.cs.postln };

    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLogicalTie({ |logicalTie| logicalTie.isPitched.not });
    x.do { |each| each.cs.postln };

    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLogicalTie({ |logicalTie| logicalTie.isTrivial.not });
    x.do { |each| each.cs.postln };

    m = FoscLeafMaker().([60, 60, [62, 64], nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLogicalTie; //({ |logicalTie| logicalTie.isTrivial.not });
    x.do { |each| each.cs.postln };

   
    ################################################
    m = FoscTupletSpellingSpecifier(simplifyRedundantTuplets: true);
    a = Threads_rtm('ratio', tupletSpellingSpecifier: m);
    a = a.(3/8 ! 3, [7, 2]);
    a.show;
    ################################################
    a = FoscTupletMaker().(3/8 ! 3, [[1,1,1,1,1]]);
    a.byLeaf[0..6].attach(FoscTie());
    a.byLeaf[7..14].attach(FoscTie());
    x = FoscSelection(a).byClass(FoscTuplet);
    x.do { |tuplet| tuplet.prSimplifyRedundantTuplet };
    a.show;
    ################################################
    a = FoscTupletMaker().([1/4], [[3, 2.0], [2.0, 3.0], [3, 2.0]]);
    FoscIterationAgent(a).byLogicalTie.do { |each| each.postln };
    ################################################
    m = FoscLeafMaker().([60, 60, 62, nil], [1/4, 2/4, 5/4, 3/4]);
    x = FoscIterationAgent(m).byLogicalTie;
    x.do { |each| [each, each.duration.str].postln };
    -------------------------------------------------------------------------------------------------------- */
    byLogicalTie_OLD { |condition=true, parentageMask|  
        var leaf, yielded, tieSpanners, isTied, isLastLeafInTie, logicalTie;
        routine = Routine {
            // yielded = False
            // tie_spanners = leaf._get_spanners(spannertools.Tie)
            // if (not tie_spanners or
            //     tuple(tie_spanners)[0]._is_my_last_leaf(leaf)):
            //     logical_tie = leaf._get_logical_tie()
            //     if parentage_mask:
            //         logical_tie = selectiontools.LogicalTie(
            //             x for x in logical_tie
            //             if parentage_mask in x._get_parentage()
            //             )
            //         if not logical_tie:
            //             continue
            //     # if not nontrivial or not logical_tie.is_trivial:
            //     if not logical_tie.is_trivial:
            //         yielded = True
            //         yield logical_tie
            //this.byClass([FoscLeaf]).do { |leaf|
            client.prIterateTopDown.do { |component|
                if (component.isKindOf(FoscLeaf)) {
                    //##### "______________".postln; Post.nl;
                    leaf = component;
                    //outerLeaf = leaf;
                    yielded = false;
                    tieSpanners = leaf.prSpanners(FoscTie);
                    isTied = tieSpanners.notEmpty;
                    isLastLeafInTie = if (isTied) { leaf == tieSpanners.last };
                    if (isTied.not || isLastLeafInTie) {
                        logicalTie = leaf.prGetLogicalTie;
                        if (parentageMask.notNil) {
                            logicalTie.selectInPlace { |each| each.prGetParentage.includes(parentageMask) };
                            //##### [\m0, logicalTie].postln;
                            if (logicalTie.notEmpty) {
                                logicalTie = FoscLogicalTie(logicalTie);
                            } {
                            };
                        };
                        if (condition.value(logicalTie)) {
                            //##### [\m1, logicalTie].postln;
                            yielded = true;
                            logicalTie.yield;
                        };
                    };
                };
            };
            // if leaf is not None and not yielded:
            //     if (tie_spanners and
            //         tuple(tie_spanners)[0]._is_my_first_leaf(leaf)):
            //         logical_tie = leaf._get_logical_tie()
            //         if parentage_mask:
            //             logical_tie = selectiontools.LogicalTie(
            //                 x for x in logical_tie
            //                 if parentage_mask in x._get_parentage()
            //                 )
            //             if not logical_tie:
            //                 return
            //         # if not nontrivial or not logical_tie.is_trivial:
            //         if not logical_tie.is_trivial:
            //             yield logical_tie
            //##### [\o0, leaf.notNil, yielded.not].postln;
            if (leaf.notNil && yielded.not) {
                tieSpanners = leaf.prSpanners(FoscTie); //#### not in abjad
                isTied = tieSpanners.notEmpty;
                //##### [\o1, isTied, tieSpanners].postln;
                //### when following if statement is included, the final logical tie is not caught
                //### if (isTied && { tieSpanners.pop.prIsMyFirstLeaf(leaf) }) {
                    logicalTie = leaf.prGetLogicalTie;
                    if (parentageMask.notNil) {
                        logicalTie.selectInPlace { |each| each.prGetParentage.includes(parentageMask) };
                        if (logicalTie.notEmpty) {
                            logicalTie = FoscLogicalTie(logicalTie);
                            //### [\o2, logicalTie, logicalTie.music].postln;
                        };
                    };
                    if (condition.value(logicalTie)) {
                        //##### [\o3, logicalTie, logicalTie.music].postln;
                        logicalTie.yield;
                    };
                //### };
            };
        };
    }
    // byLogicalTie { |condition=true|  
    //     var tieSpanners, isTied, isLastLeafInTie, logicalTie;
    //     routine = Routine {
    //         client.prIterateTopDown.do { |component|
    //             if (component.isKindOf(FoscLeaf)) {
    //                 tieSpanners = component.prSpanners(FoscTie);
    //                 isTied = tieSpanners.notEmpty;
    //                 isLastLeafInTie = if (isTied) { tieSpanners.as(Array)[0].prIsMyLastLeaf(component) };
    //                 if (isTied.not || isLastLeafInTie) {
    //                     logicalTie = component.prGetLogicalTie;
    //                     if (condition.value(logicalTie)) { logicalTie.yield };
    //                 };
    //             };
    //         };
    //     };
    // }
    /* --------------------------------------------------------------------------------------------------------
    • byLogicalVoice

    Iterates by logical voice.

    def by_logical_voice(
        self,
        prototype,
        logical_voice,
        reverse=False,
        ):
        if self._expression:
            return self._update_expression(inspect.currentframe())
        def _closure():
            if (isinstance(self._client, prototype) and
                self._client._get_parentage().logical_voice == logical_voice):
                yield self._client
            if not reverse:
                if isinstance(self._client, (list, tuple)):
                    for component in self._client:
                        for x in iterate(component).by_logical_voice(
                            prototype,
                            logical_voice,
                            ):
                            yield x
                if hasattr(self._client, '_music'):
                    for component in self._client._music:
                        for x in iterate(component).by_logical_voice(
                            prototype,
                            logical_voice,
                            ):
                            yield x
            else:
                if isinstance(self._client, (list, tuple)):
                    for component in reversed(self._client):
                        for x in iterate(component).by_logical_voice(
                            prototype,
                            logical_voice,
                            reverse=True,
                            ):
                            yield x
                if hasattr(self._client, '_music'):
                    for component in reversed(self._client._music):
                        for x in iterate(component).by_logical_voice(
                            prototype,
                            logical_voice,
                            reverse=True,
                            ):
                            yield x
        return _closure()
    
    
    a = FoscContainer([
        FoscVoice({ |i| FoscNote(60 + i, [1, 4]) } ! 4),
        FoscVoice({ |i| FoscNote(70 + i, [1, 4]) } ! 4)
    ]);

    x = FoscInspection(a.select.byLeaf.first).parentage.logicalVoice;
    iterate(a).byLogicalVoice(FoscNote, logicalVoice: x).do { |each| each.inspect };

    y = FoscInspection(a.select.byLeaf.last).parentage.logicalVoice;
    iterate(a).byLogicalVoice(FoscNote, logicalVoice: y).do { |each| each.inspect };
    -------------------------------------------------------------------------------------------------------- */
    byLogicalVoice { |prototype, logicalVoice|
        if (prototype.isSequenceableCollection.not) { prototype = [prototype] };
        
        routine = Routine {
            if (prototype.any { |type| client.isKindOf(type) }
                && client.prGetParentage.logicalVoice == logicalVoice) {
                client.yield;
            };
            if (client.isSequenceableCollection) {
                client.do { |component|
                    iterate(component).byLogicalVoice(prototype, logicalVoice).do { |each|
                        each.yield;
                    };
                };
            };
            if (client.respondsTo('music')) {
                // client.music.reverseDo { |component|
                //     iterate(component).byLogicalVoice(prototype, logicalVoice).reverseDo { |each|
                //         each.yield;
                //     };
                // };
                client.components.do { |component|
                    iterate(component).byLogicalVoice(prototype, logicalVoice).do { |each|
                        each.yield;
                    };
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • byLogicalVoiceFromComponent
    
    Iterates by logical voice from client.

    def by_logical_voice_from_component(
            self,
            prototype=None,
            reverse=False,
            ):
        if self._expression:
            return self._update_expression(inspect.currentframe())
        # set default class
        if prototype is None:
            prototype = scoretools.Component
        def _closure():
            # save logical voice signature of input component
            signature = self._client._get_parentage().logical_voice
            # iterate component depth-first allowing to crawl UP into score
            if not reverse:
                for x in iterate(self._client).depth_first(capped=False):
                    if isinstance(x, prototype):
                        if x._get_parentage().logical_voice == signature:
                            yield x
            else:
                for x in iterate(self._client).depth_first(
                    capped=False,
                    direction=Right,
                    ):
                    if isinstance(x, prototype):
                        if x._get_parentage().logical_voice == signature:
                            yield x
        return _closure()
    

    a = FoscContainer([
        FoscVoice({ |i| FoscNote(60 + i, [1, 4]) } ! 4),
        FoscVoice({ |i| FoscNote(70 + i, [1, 4]) } ! 4)
    ]);

    c = a.select.byLeaf.first;
    iterate(c).byLogicalVoiceFromComponent.do { |each| each.inspect };

    c = a.select.byLeaf[2];
    iterate(c).byLogicalVoiceFromComponent.do { |each| each.inspect };

    c = a[1];
    iterate(c).byLogicalVoiceFromComponent.do { |each| each.inspect };

    c = a.select.byLeaf.last;
    iterate(c).byLogicalVoiceFromComponent(reverse: true).do { |each| each.inspect };

    c = a.select.byLeaf.last;
    iterate(a[0]).byLogicalVoiceFromComponent(reverse: true).do { |each| each.inspect };
    -------------------------------------------------------------------------------------------------------- */
    byLogicalVoiceFromComponent { |prototype, reverse=false|
        var signature, iterable;
        // if self._expression:
        //    return self._update_expression(inspect.currentframe())
        prototype = prototype ? [FoscComponent];
        if (prototype.isSequenceableCollection.not) { prototype = [prototype] };
        signature = client.prGetParentage.logicalVoice;
        if (reverse.not) {
            iterable = FoscIterationAgent(client).depthFirst(capped: false);
        } {
            iterable = FoscIterationAgent(client).depthFirst(capped: false, direction: 'right');   
        };
        routine = Routine {
            iterable.do { |component|
                if (prototype.any { |type| component.isKindOf(type) }
                    && (component.prGetParentage.logicalVoice == signature)) {
                    component.yield;
                };
            };
        };      
    }
    /* --------------------------------------------------------------------------------------------------------
    • byPitch

    Iterates by pitch.

    def by_pitch(self):
        from abjad.tools import pitchtools
        from abjad.tools import scoretools
        from abjad.tools import spannertools
        from abjad.tools.topleveltools import iterate
        if self._expression:
            return self._update_expression(inspect.currentframe())
        def _closure():
            if isinstance(self._client, pitchtools.Pitch):
                pitch = pitchtools.NamedPitch.from_pitch_carrier(self._client)
                yield pitch
            result = []
            if hasattr(self._client, 'written_pitches'):
                result.extend(self._client.written_pitches)
            # for pitch arrays
            elif hasattr(self._client, 'pitches'):
                result.extend(self._client.pitches)
            elif isinstance(self._client, spannertools.Spanner):
                for leaf in self._client._get_leaves():
                    if (hasattr(leaf, 'written_pitch') and
                        not isinstance(leaf, scoretools.Rest)):
                        result.append(leaf.written_pitch)
                    elif hasattr(leaf, 'written_pitches'):
                        result.extend(leaf.written_pitches)
            elif isinstance(self._client, pitchtools.PitchSet):
                result.extend(sorted(list(self._client)))
            elif isinstance(self._client, (list, tuple, set)):
                for item in self._client:
                    for pitch_ in iterate(item).by_pitch():
                        result.append(pitch_)
            else:
                for leaf in iterate(self._client).by_leaf():
                    if (hasattr(leaf, 'written_pitch') and
                        not isinstance(leaf, scoretools.Rest)):
                        result.append(leaf.written_pitch)
                    elif hasattr(leaf, 'written_pitches'):
                        result.extend(leaf.written_pitches)
            for pitch in result:
                yield pitch
        return _closure()
    -------------------------------------------------------------------------------------------------------- */
    byPitch {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byPitchedLeaf

    Iterates by pitched logical tie.
    
    • reworked using only condition function as argument

    a = FoscStaff([
        FoscNote(60, [1, 4]),
        FoscNote(61, [1, 4]),
        FoscNote(62, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(63, [1, 4]),
        FoscNote(64, [1, 4])
    ]);
    b = iterate(a).byPitchedLeaf;
    b.do { |each| each.inspect };

    a = FoscStaff([
        FoscNote(60, [1, 4]),
        FoscNote(61, [1, 4]),
        FoscNote(62, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(63, [1, 4]),
        FoscNote(64, [1, 4])
    ]);
    b = iterate(a).byPitchedLeaf(condition: { |leaf| leaf.pitch.pitchNumber > 61 });
    b.do { |each| each.inspect };
    -------------------------------------------------------------------------------------------------------- */
    byPitchedLeaf { |condition=true|
        this.byClass([FoscLeaf], { |leaf| leaf.isPitched && { condition.value(leaf) } });
    }
    /* --------------------------------------------------------------------------------------------------------
    • byPitchedLogicalTie

    Iterates by pitched logical tie.
    
    • reworked using only condition function as argument

    a = FoscNote(60, 1);
    b = FoscNote(60, 1);
    c = FoscNote(60, 3);
    d = FoscRest(4);
    FoscSelection([a, b]).attach(FoscTie());
    m = FoscContainer([a, b, c, d]);
    x = iterate(m).byPitchedLogicalTie;
    x.reset.all.do { |each| each.duration.pair.postln };
    -------------------------------------------------------------------------------------------------------- */
    byPitchedLogicalTie { |condition=true|
        this.byLogicalTie({ |logicalTie| logicalTie.isPitched && { condition.value(logicalTie) } });
    }
    /* --------------------------------------------------------------------------------------------------------
    • byPitchedRun

    a = FoscStaff([
        FoscChord([60, 63], [1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4])
    ]);
    x = iterate(a).byPitchedRun;
    x.all.do { |each| each.music.postln };
    -------------------------------------------------------------------------------------------------------- */
    byPitchedRun { |condition=true|
        //this.byRun({ |run| run.every { |elem| elem.isPitched } && { condition.value(run) } });
        this.byRun({ |run| run[0].isPitched && { condition.value(run) } });
    }
    /* --------------------------------------------------------------------------------------------------------
    • byPitchPair

    Iterates by pitch pair.

    def by_pitch_pair(self):
        import abjad
        if self._expression:
            return self._update_expression(inspect.currentframe())
        def _closure():
            for leaf_pair in self.by_leaf_pair():
                leaf_pair_list = list(leaf_pair)
                # iterate chord pitches if first leaf is chord
                for pair in self._list_unordered_pitch_pairs(
                    leaf_pair_list[0]):
                    yield abjad.PitchSegment(items=pair)
                if isinstance(leaf_pair, set):
                    for pair in self._list_unordered_pitch_pairs(leaf_pair):
                        yield abjad.PitchSegment(items=pair)
                else:
                    for pair in self._list_ordered_pitch_pairs(*leaf_pair):
                        yield abjad.PitchSegment(items=pair)
                # iterate chord pitches if last leaf is chord
                for pair in self._list_unordered_pitch_pairs(
                    leaf_pair_list[1]):
                    yield abjad.PitchSegment(items=pair)
        return _closure()
    -------------------------------------------------------------------------------------------------------- */
    byPitchPair {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byRun

    Iterates by run of components of the same type.

    Iterates leaves only, not all nodes. This is different to the equivalent method in abjad.

    def by_run(self, prototype=None):
        from abjad.tools import scoretools
        from abjad.tools import selectiontools
        if self._expression:
            return self._update_expression(inspect.currentframe())
        prototype = prototype or scoretools.Leaf
        if not isinstance(prototype, collections.Sequence):
            prototype = (prototype,)
        sequence = selectiontools.Selection(self._client)
        def _closure():
            current_run = ()
            for run in sequence.group_by(type):
                if isinstance(run[0], prototype):
                    current_run = current_run + run
                elif current_run:
                    yield current_run
                    current_run = ()
            if current_run:
                yield current_run
        return _closure()



    a = FoscStaff([
        FoscChord([60, 63], [1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4]),
        FoscRest([1, 4]),
        FoscRest([1, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4])
    ]);
    x = iterate(a).byRun;
    x.all.do { |each| each.music.postln };
    -------------------------------------------------------------------------------------------------------- */
    byRun { |condition=true|
        var prototype, currentRun, prev, currentIsPitched, previousIsPitched;
        prototype = [FoscNote, FoscChord];
        routine = Routine {
            currentRun = [];
            client.prIterateTopDown.do { |component|
                if (component.isKindOf(FoscLeaf)) {
                    currentIsPitched = prototype.any { |type| component.isKindOf(type) };
                    previousIsPitched = prototype.any { |type| prev.isKindOf(type) };
                    if (currentIsPitched != previousIsPitched && { prev.notNil }) {
                        if (condition.value(currentRun)) {
                            FoscSelection(currentRun).yield;
                        }; 
                        currentRun = [component];
                    } {
                        currentRun = currentRun.add(component);
                    };
                    prev = component;
                };
            };
            if (condition.value(currentRun)) {
                FoscSelection(currentRun).yield;
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • bySemanticVoice

    def by_semantic_voice(
        self,
        reverse=False,
        start=0,
        stop=None,
        ):
        if self._expression:
            return self._update_expression(inspect.currentframe())
        result = []
        for voice in self.by_class(
            scoretools.Voice,
            reverse=reverse,
            start=start,
            stop=stop,
            ):
            if not voice.is_nonsemantic:
                #yield voice
                result.append(voice)
        return result
    -------------------------------------------------------------------------------------------------------- */
    bySemanticVoice {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • bySpanner

    Iterates by spanner.

    def by_spanner(
            self,
            prototype=None,
            reverse=False,
            ):
        if self._expression:
            return self._update_expression(inspect.currentframe())
        def _closure():
            visited_spanners = set()
            for component in self.by_class(reverse=reverse):
                spanners = inspect_(component).get_spanners(prototype=prototype)
                spanners = sorted(spanners,
                    key=lambda x: (
                        type(x).__name__,
                        inspect_(x).get_timespan(),
                        ),
                    )
                for spanner in spanners:
                    if spanner in visited_spanners:
                        continue
                    visited_spanners.add(spanner)
                    yield spanner
        return _closure()
    -------------------------------------------------------------------------------------------------------- */
    bySpanner {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byTimeline

    Iterates by timeline.

    # TODO: optimize to avoid behind-the-scenes full-score traversal.
    def by_timeline(self, prototype=None, reverse=False):
        if self._expression:
            return self._update_expression(inspect.currentframe())
        prototype = prototype or scoretools.Leaf
        def _closure():
            if isinstance(self.client, scoretools.Component):
                components = [self.client]
            else:
                components = list(self.client)
            while components:
                current_start_offset = min(
                    _._get_timespan().start_offset
                    for _ in components
                    )
                components.sort(
                    key=lambda x: x._get_parentage(with_grace_notes=True).score_index,
                    reverse=True,
                    )
                components_to_process = components[:]
                components = []
                while components_to_process:
                    component = components_to_process.pop()
                    start_offset = component._get_timespan().start_offset
                    #print('    COMPONENT:', component)
                    if current_start_offset < start_offset:
                        components.append(component)
                        #print('        TOO EARLY')
                        continue
                    if isinstance(component, prototype):
                        #print('        YIELDING', component)
                        yield component
                    sibling = component._get_sibling(1)
                    if sibling is not None:
                        #print('        SIBLING:', sibling)
                        components.append(sibling)
                    if not isinstance(component, scoretools.Container):
                        continue
                    if not len(component):
                        continue
                    if not component.is_simultaneous:
                        components_to_process.append(component[0])
                    else:
                        components_to_process.extend(reversed(component))
        return _closure()
    
    a = FoscStaff([
        FoscChord([60, 63], [1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4])
    ]);
    b = FoscStaff([
        FoscNote(60, [1, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4]),
        FoscRest([1, 4]),
        FoscRest([2, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4])
    ]);
    c = FoscScore([a, b]);

    x = FoscIterationAgent(c).byTimeline([FoscLeaf]);
    x.all.do { |each, i| each.attach(FoscMarkup(i.asString, \above)) };
    c.show;

    x = FoscIterationAgent(c).byTimeline([FoscLeaf], { |leaf| leaf.isPitched });
    x.all.do { |each, i| each.attach(FoscMarkup(i.asString, \above)) };
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    //TODO!!!!!: NOT CURRENTLY WORKING
    byTimeline { |prototype, condition=true|
        // var iterable, offsetWithScoreIndices, order;
        // if (client.isKindOf(FoscComponent) || { client.isKindOf(FoscSelection) }) {
        //     iterable = client.prIterateTopDown.all;
        // };
        // // brute-force order by offset and score-index
        // offsetWithScoreIndices = iterable.collect { |each|
        //     [each.prGetTimespan.startOffset].addAll(each.prGetParentage(graceNotes: true).scoreIndex);
        // };
        // order = offsetWithScoreIndices.orderN;
        // iterable = iterable[order];
        // this.byClass(prototype, condition);

        var iterable, offsetWithScoreIndices, order;
        // brute-force order by offset and score-index
        iterable = all(FoscIterationAgent(client));
        offsetWithScoreIndices = iterable.collect { |each|
            [each.prGetTimespan.startOffset] ++ each.prGetParentage(graceNotes: true).scoreIndex;
        };
        order = offsetWithScoreIndices.orderN;
        iterable = iterable[order];
        ^FoscIterationAgent(iterable).byClass(prototype, condition);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byTimelineAndLogicalTie

    Iterates by timeline and logical tie.

    a = FoscStaff([
        FoscChord([60, 63], [1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4])
    ]);
    b = FoscStaff([
        FoscNote(60, [1, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4]),
        FoscRest([1, 4]),
        FoscRest([2, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4])
    ]);
    c = FoscScore([a, b]);

    x = FoscIterationAgent(c).byTimelineAndLogicalTie;
    x.do { |each, i| each[0].attach(FoscMarkup(i.asString, \above)) };
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    byTimelineAndLogicalTie { |condition=true|
        var iterable, offsetWithScoreIndices, order;
        // brute-force order by offset and score-index
        iterable = all(FoscIterationAgent(client).byLogicalTie);
        offsetWithScoreIndices = iterable.collect { |each|
            [each.head.prGetTimespan.startOffset] ++ each.head.prGetParentage(graceNotes: true).scoreIndex;
        };
        order = offsetWithScoreIndices.orderN;
        iterable = iterable[order];
        ^FoscIterationAgent(iterable).byLogicalTie(condition);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byTimelineAndPitchedLogicalTie

    Iterates by timeline and pitched logical tie.

    a = FoscStaff([
        FoscChord([60, 63], [1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4])
    ]);
    b = FoscStaff([
        FoscNote(60, [1, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4]),
        FoscRest([1, 4]),
        FoscRest([2, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4])
    ]);
    c = FoscScore([a, b]);

    x = FoscIterationAgent(c).byTimelineAndPitchedLogicalTie;
    x.do { |each, i| each.head.attach(FoscMarkup(i.asString, \above)) };
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    byTimelineAndPitchedLogicalTie { |condition=true|
        var iterable, offsetWithScoreIndices, order;
        // brute-force order by offset and score-index
        iterable = all(FoscIterationAgent(client).byPitchedLogicalTie(condition));
        offsetWithScoreIndices = iterable.collect { |each|
            [each.head.prGetTimespan.startOffset] ++ each.head.prGetParentage(graceNotes: true).scoreIndex;
        };
        order = offsetWithScoreIndices.orderN;
        iterable = iterable[order];
        ^FoscIterationAgent(iterable).byPitchedLogicalTie(condition);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byTimelineFromComponent

    Iterates from client by timeline.

    # TODO: optimize to avoid behind-the-scenes full-score traversal
    def by_timeline_from_component(
        self,
        prototype=None,
        reverse=False,
        ):
        if self._expression:
            return self._update_expression(inspect.currentframe())
        assert isinstance(self._client, scoretools.Component)
        prototype = prototype or scoretools.Leaf
        root = self._client._get_parentage().root
        component_generator = iterate(root).by_timeline(
            prototype=prototype,
            reverse=reverse,
            )
        def _closure():
            yielded_expr = False
            for component in component_generator:
                if yielded_expr:
                    yield component
                elif component is self._client:
                    yield component
                    yielded_expr = True
        return _closure()
    -------------------------------------------------------------------------------------------------------- */
    byTimelineFromComponent {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byTopmostLogicalTiesAndComponents

    Iterates by topmost logical ties and components.

    def by_topmost_logical_ties_and_components(self):
        from abjad.tools import selectiontools
        prototype = (spannertools.Tie,)
        if self._expression:
            return self._update_expression(inspect.currentframe())
        def _closure():
            if isinstance(self._client, scoretools.Leaf):
                logical_tie = self._client._get_logical_tie()
                if len(logical_tie) == 1:
                    yield logical_tie
                else:
                    message = 'can not have only one leaf in logical tie.'
                    raise ValueError(message)
            elif isinstance(
                self._client, (
                    collections.Sequence,
                    scoretools.Container,
                    selectiontools.Selection,
                    )):
                for component in self._client:
                    if isinstance(component, scoretools.Leaf):
                        tie_spanners = component._get_spanners(prototype)
                        if not tie_spanners or \
                            tuple(tie_spanners)[0]._is_my_last_leaf(component):
                            yield component._get_logical_tie()
                    elif isinstance(component, scoretools.Container):
                        yield component
            else:
                message = 'input must be iterable: {!r}.'
                message = message.format(self._client)
                raise ValueError(message)
        return _closure()
    -------------------------------------------------------------------------------------------------------- */
    byTopmostLogicalTiesAndComponents {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • byVerticalMoment

    Iterates by vertical moment.

    def by_vertical_moment(self, reverse=False):
        from abjad.tools import selectiontools
        if self._expression:
            return self._update_expression(inspect.currentframe())
        def _buffer_components_starting_with(component, buffer, stop_offsets):
            #if not isinstance(component, scoretools.Component):
            #    raise TypeError
            buffer.append(component)
            stop_offsets.append(component._get_timespan().stop_offset)
            if isinstance(component, scoretools.Container):
                if component.is_simultaneous:
                    for x in component:
                        _buffer_components_starting_with(
                            x, buffer, stop_offsets)
                else:
                    if component:
                        _buffer_components_starting_with(
                            component[0], buffer, stop_offsets)
        def _iterate_vertical_moments(argument):
            #if not isinstance(argument, scoretools.Component):
            #    raise TypeError
            governors = (argument,)
            current_offset, stop_offsets, buffer = \
                durationtools.Offset(0), [], []
            _buffer_components_starting_with(argument, buffer, stop_offsets)
            while buffer:
                vertical_moment = selectiontools.VerticalMoment()
                offset = durationtools.Offset(current_offset)
                components = list(buffer)
                components.sort(key=lambda x: x._get_parentage().score_index)
                vertical_moment._offset = offset
                vertical_moment._governors = governors
                vertical_moment._components = components
                yield vertical_moment
                current_offset, stop_offsets = min(stop_offsets), []
                _update_buffer(current_offset, buffer, stop_offsets)
        def _next_in_parent(component):
            from abjad.tools import selectiontools
            if not isinstance(component, scoretools.Component):
                raise TypeError
            selection = selectiontools.Selection(component)
            parent, start, stop = \
                selection._get_parent_and_start_stop_indices()
            assert start == stop
            if parent is None:
                raise StopIteration
            # can not advance within simultaneous parent
            if parent.is_simultaneous:
                raise StopIteration
            try:
                return parent[start + 1]
            except IndexError:
                raise StopIteration
        def _update_buffer(current_offset, buffer, stop_offsets):
            #print 'At %s with %s ...' % (current_offset, buffer)
            for component in buffer[:]:
                if component._get_timespan().stop_offset <= current_offset:
                    buffer.remove(component)
                    try:
                        next_component = _next_in_parent(component)
                        _buffer_components_starting_with(
                            next_component, buffer, stop_offsets)
                    except StopIteration:
                        pass
                else:
                    stop_offsets.append(component._get_timespan().stop_offset)
        def _closure():
            if not reverse:
                for x in _iterate_vertical_moments(self._client):
                    yield x
            else:
                moments_in_governor = []
                for component in self.by_class():
                    offset = component._get_timespan().start_offset
                    if offset not in moments_in_governor:
                        moments_in_governor.append(offset)
                moments_in_governor.sort()
                for moment_in_governor in reversed(moments_in_governor):
                    yield self._client._get_vertical_moment_at(moment_in_governor)
        return _closure()
    


    a = FoscStaff([
        FoscChord([60, 63], [1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4]),
        FoscNote(60, [1, 4]),
        FoscRest([1, 4]),
        FoscNote(60, [1, 4])
    ]);
    b = FoscStaff([
        FoscNote(60, [1, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4]),
        FoscRest([1, 4]),
        FoscRest([2, 4]),
        FoscChord([60, 63], [1, 4]),
        FoscRest([1, 4])
    ]);
    c = FoscScore([a, b]);

    x = FoscIterationAgent(c).byVerticalMoment;
    x.do { |each, i| each[0].attach(FoscMarkup(i.asString, \above)) };
    c.show;

    x = [[0], [0, 0], [1, 0], [1, 0, 0], [0, 0, 1, 2]];
    x.sortN.printAll;
    -------------------------------------------------------------------------------------------------------- */
    //!!!TODO: NOT CURRENTLY WORKING
    byVerticalMoment { |reverse=false|
        var bufferComponentsStartingWith, iterateVerticalMoments, nextInParent, updateBuffer, closure;
        var governors, currentOffset, stopOffsets, buffer, verticalMoment, offset, components, selection;
        var parent, start, stop, nextComponent;

        // if self._expression:
        //     return self._update_expression(inspect.currentframe())
        
        // def _buffer_components_starting_with(component, buffer, stop_offsets):
        //     #if not isinstance(component, scoretools.Component):
        //     #    raise TypeError
        //     buffer.append(component)
        //     stop_offsets.append(component._get_timespan().stop_offset)
        //     if isinstance(component, scoretools.Container):
        //         if component.is_simultaneous:
        //             for x in component:
        //                 _buffer_components_starting_with(
        //                     x, buffer, stop_offsets)
        //         else:
        //             if component:
        //                 _buffer_components_starting_with(
        //                     component[0], buffer, stop_offsets)

        bufferComponentsStartingWith = { |component, buffer, stopOffsets|
            //!!! assert component.isKindOf(FoscComponent)
            // abjad: buffer.add(component);
            if (component.parent.notNil) { buffer.add(component) };
            stopOffsets = stopOffsets.add(component.prGetTimespan.stopOffset);
            if (component.isKindOf(FoscContainer)) {
                case
                { component.isSimultaneous } {
                    component.do { |each| bufferComponentsStartingWith.(each, buffer, stopOffsets) };
                }
                { component.notNil } {
                    bufferComponentsStartingWith.(component[0], buffer, stopOffsets);
                };     
            };
        };

        // def _iterate_vertical_moments(argument):
        //     #if not isinstance(argument, scoretools.Component):
        //     #    raise TypeError
        //     governors = (argument,)
        //     current_offset, stop_offsets, buffer = \
        //         durationtools.Offset(0), [], []
        //     _buffer_components_starting_with(argument, buffer, stop_offsets)
        //     while buffer:
        //         vertical_moment = selectiontools.VerticalMoment()
        //         offset = durationtools.Offset(current_offset)
        //         components = list(buffer)
        //         components.sort(key=lambda x: x._get_parentage().score_index)
        //         vertical_moment._offset = offset
        //         vertical_moment._governors = governors
        //         vertical_moment._components = components
        //         yield vertical_moment
        //         current_offset, stop_offsets = min(stop_offsets), []
        //         _update_buffer(current_offset, buffer, stop_offsets)

        iterateVerticalMoments = { |expr|
            var order; //!!!!
            //!!! assert expr.isKindOf(FoscComponent)
            governors = [expr];
            # currentOffset, stopOffsets, buffer = [FoscOffset(0), [], List[]];
            bufferComponentsStartingWith.(expr, buffer, stopOffsets);
            Routine {
                while { buffer.notEmpty } {
                    verticalMoment = FoscVerticalMoment();
                    offset = FoscOffset(currentOffset);
                    components = buffer;
                    //!!!!
                    \a.postln;
                    "components: ".post; components.postln;
                    //!!!!
                    // components = components.sort { |a, b|
                    //     //!!!
                    //     [a.prGetParentage.scoreIndex, b.prGetParentage.scoreIndex].postln;
                    //     //!!!
                    //     a.prGetParentage.scoreIndex < b.prGetParentage.scoreIndex;
                    // };
                    order = components.collect { |each| each.prGetParentage.scoreIndex.postln }.orderN;
                    //!!!
                    "order: ".post; order.postln;
                    components = components[order];

                    verticalMoment.prOffset_(offset);
                    verticalMoment.prGovernors_(governors);
                    verticalMoment.prComponents_(components);
                    \b.postln;
                    // yield verticalMoment
                    // verticalMoment.yield;
                    \c.postln;
                    # currentOffset, stopOffsets = [stopOffsets.minItem, []];
                    updateBuffer.(currentOffset, buffer, stopOffsets);
                    \d.postln;
                };
            };
        };

        // def _next_in_parent(component):
        //     from abjad.tools import selectiontools
        //     if not isinstance(component, scoretools.Component):
        //         raise TypeError
        //     selection = selectiontools.Selection(component)
        //     parent, start, stop = \
        //         selection._get_parent_and_start_stop_indices()
        //     assert start == stop
        //     if parent is None:
        //         raise StopIteration
        //     # can not advance within simultaneous parent
        //     if parent.is_simultaneous:
        //         raise StopIteration
        //     try:
        //         return parent[start + 1]
        //     except IndexError:
        //         raise StopIteration

        nextInParent = { |component|
            //!!! assert component.isKindOf(FoscComponent)  
            selection = FoscSelection(component);
            # parent, start, stop = selection.prGetParentAndStartStopIndices;
            assert (start == stop);
            if (parent.isNil) { /* raise StopIteration */ };
            // can not advance within simultaneous parent
            if (parent.isSimultaneous) {
                /* raise StopIteration */ 
            } {
                try {
                    ^parent[start + 1];
                } {
                    throw("%:%: index error.".format(this.species, thisMethod.name));
                };
            };
        };

        // def _update_buffer(current_offset, buffer, stop_offsets):
        //     #print 'At %s with %s ...' % (current_offset, buffer)
        //     for component in buffer[:]:
        //         if component._get_timespan().stop_offset <= current_offset:
        //             buffer.remove(component)
        //             try:
        //                 next_component = _next_in_parent(component)
        //                 _buffer_components_starting_with(
        //                     next_component, buffer, stop_offsets)
        //             except StopIteration:
        //                 pass
        //         else:
        //             stop_offsets.append(component._get_timespan().stop_offset)

        updateBuffer = { |currentOffset, buffer, stopOffsets|
            buffer.do { |component|
                if (component.prGetTimespan.stopOffset <= currentOffset) {
                    buffer.remove(component);
                    try {
                        nextComponent = nextInParent.(component);
                        bufferComponentsStartingWith.(nextComponent, buffer, stopOffsets);
                    } {
                        // pass
                    }
                } {
                    stopOffsets = stopOffsets.add(component.prGetTimespan.stopOffset);
                };
            };
        };

        // def _closure():
        //     if not reverse:
        //         for x in _iterate_vertical_moments(self._client):
        //             yield x
        //     else:
        //         moments_in_governor = []
        //         for component in self.by_class():
        //             offset = component._get_timespan().start_offset
        //             if offset not in moments_in_governor:
        //                 moments_in_governor.append(offset)
        //         moments_in_governor.sort()
        //         for moment_in_governor in reversed(moments_in_governor):
        //             yield self._client._get_vertical_moment_at(moment_in_governor)

        closure = {
            if (reverse.not) {
                all(iterateVerticalMoments.(client));
            } {

            };
        };

        // return _closure()
        ^closure.value;
    }
    /* --------------------------------------------------------------------------------------------------------
    • depthFirst

    Iterates depth first.

    a = FoscStaff(FoscLeafMaker().((60..65), [1/4]));
    iterate(a).depthFirst.do { |each| each.postln };
    iterate(a).depthFirst(direction: 'right').do { |each| each.postln };
    -------------------------------------------------------------------------------------------------------- */
    depthFirst { |capped=true, direction='left', forbid, unique=true|
        var nextNodeDepthFirst, previousNodeDepthFirst, handleForbiddenNode, advanceNodeDepthFirst;
        var isNodeForbidden, findYield;
        //!!! if (this.expression) { ^this.prUpdateExpression(inspect.currentFrame) };
        nextNodeDepthFirst = { |component, total|
            var result, parent, carrier, carrierParent;
            case
            { component.respondsTo('components') && { component.size > 0 } && { component.size > total } } {
                result = [component[total], 0]; // return next not-yet-returned child
            }           
            { component.respondsTo('graceContainer') && { component.graceContainer.notNil } } {
                result = [component.graceContainer, 0];
            }
            { component.respondsTo('afterGraceContainer') && { component.afterGraceContainer.notNil } } {
                result = [component.afterGraceContainer, 0];
            }
            { component.respondsTo('carrier') } {
                carrier = component.carrier;
                if (carrier.isNil) { 
                    result = [nil, nil]
                } {
                    if (component.isKindOf(FoscAfterGraceContainer).not
                        && { carrier.afterGraceContainer.notNil }) { 
                        result = [carrier.afterGraceContainer, 0];
                    } {
                        carrierParent = carrier.parent;
                        if (carrierParent.isNil) { 
                            result = [nil, nil];
                        } {
                            result = [carrierParent, carrierParent.indexOf(carrier) + 1];
                        };
                    };
                };
            }
            {
                parent = component.parent;
                if (parent.isNil) {
                    result = [nil, nil];
                } {
                    result = [parent, parent.indexOf(component) + 1];
                };
            };
            result;
        };

        previousNodeDepthFirst = { |component, total=0|
            var result, parent;
            if (component.respondsTo('components') && { component.size > 0 } && { component.size > total }) {
                result = [component[component.size - 1 - total], 0];
            } {
                parent = component.parent;
                if (parent.notNil) {
                    result = [parent, parent.size - parent.indexOf(component)];
                } {
                    result = [nil, nil];
                };
            };
            result;
        };

        handleForbiddenNode = { |node, queue|
            var nodeParent, rank;
            nodeParent = node.parent;
            if (nodeParent.notNil) {
                rank = nodeParent.indexOf(node) + 1;
                node = nodeParent;
            } {
                node = nil;
                rank = nil;
            };
            queue.pop;
            [node, rank];
        };

        advanceNodeDepthFirst = { |node, rank, direction|
            if (direction == 'left') {
                # node, rank = nextNodeDepthFirst.(node, rank);
            } {
                # node, rank = previousNodeDepthFirst.(node, rank);
            };
            [node, rank];
        };

        isNodeForbidden = { |node, forbid|
            var result;
            case
            { forbid.isNil } { result = false }
            { forbid == 'simultaneous' } {
                if (node.respondsTo('isSimultaneous').not) {
                    result = false;
                } {
                    result = node.isSimultaneous;
                };
            }
            { result = node.isKindOf(forbid) };
            result;
        };

        findYield = { |node, rank, queue, unique|
            var result, visited;
            if (node.respondsTo('components')) {
                try {
                    // visited = (node === queue.last); // not working
                    visited = (rank > 0);
                } {
                    visited = false;
                };
                if (visited.not || { unique != true }) {
                    queue = queue.add(node);
                    result = node;
                } { 
                    if (rank == node.size) {
                        queue.pop;
                        result = nil;
                    };
                };
            } {
                result = node;
            };
            result;
        };

        if (client.isKindOf(FoscComponent).not) {
            throw("%:%: client must be a FoscComponent: %.".format(this.species, thisMethod.name, client));
        };

        routine = Routine {
            var result, component, clientParent, node, rank, queue;
            component = client;
            clientParent = component.parent;
            node = component;
            rank = 0;
            queue = [];
            while { node.notNil && { (capped && (node === clientParent)).not } } {
                result = findYield.(node, rank, queue, unique);
                if (result.notNil) { result.yield };
                if (isNodeForbidden.(node, forbid)) {
                    # node, rank = handleForbiddenNode.(node, queue);
                } {
                    # node, rank = advanceNodeDepthFirst.(node, rank, direction);
                };
            };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prByComponentsAndGraceContainers

    def _by_components_and_grace_containers(self, prototype=None):
        prototype = prototype or scoretools.Leaf
        if getattr(self._client, '_grace', None) is not None:
            for component in self._client._grace:
                for x in iterate(component)._by_components_and_grace_containers(
                    prototype,
                    ):
                    yield x
        if isinstance(self._client, prototype):
            yield self._client
        if getattr(self._client, '_after_grace', None) is not None:
            for component in self._client._after_grace:
                for x in iterate(component)._by_components_and_grace_containers(
                    prototype,
                    ):
                    yield x
        if isinstance(self._client, (list, tuple)):
            for component in self._client:
                for x in iterate(component)._by_components_and_grace_containers(
                    prototype,
                    ):
                    yield x
        if hasattr(self._client, '_music'):
            for component in self._client._music:
                for x in iterate(component)._by_components_and_grace_containers(
                    prototype,
                    ):
                    yield x
    -------------------------------------------------------------------------------------------------------- */
    prByComponentsAndGraceContainers {
        ^this.notYetImplemented(thisMethod);
    } 
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateExpression

    def _update_expression(self, frame):
        import abjad
        callback = abjad.Expression._frame_to_callback(frame)
        return self._expression.append_callback(callback)
    -------------------------------------------------------------------------------------------------------- */
    prUpdateExpression {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prListOrderedPitchPairs

    @staticmethod
    def _list_ordered_pitch_pairs(expr_1, expr_2):
        pitches_1 = sorted(iterate(expr_1).by_pitch())
        pitches_2 = sorted(iterate(expr_2).by_pitch())
        sequences = [pitches_1, pitches_2]
        enumeration = sequencetools.Enumeration(sequences)
        for pair in enumeration.yield_outer_product():
            yield pair
    -------------------------------------------------------------------------------------------------------- */
    *prListOrderedPitchPairs {
        ^this.notYetImplemented(thisMethod);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prListUnorderedPitchPairs

    @staticmethod
    def _list_unordered_pitch_pairs(argument):
        pitches = sorted(iterate(argument).by_pitch())
        enumeration = sequencetools.Enumeration(pitches)
        for pair in enumeration.yield_pairs():
            yield pair
    -------------------------------------------------------------------------------------------------------- */
    *prListUnorderedPitchPairs {
        ^this.notYetImplemented(thisMethod);
    }
}
