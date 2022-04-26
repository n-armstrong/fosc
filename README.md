# Fosc

[supercollider]: https://supercollider.github.io/
[lilypond]: http://lilypond.org/
[abjad]: https://abjad.github.io

__Fosc__ is a [SuperCollider][supercollider] API for generating musical notation in [LilyPond][lilypond].

__Fosc__ stands for FO-rmalised S-core C-ontrol (FO-r S-uperC-ollider). It's a close relative of [Abjad][abjad], and ports much of Abjad's Python code base to SuperCollider.

__Fosc__ lets you:


* Create musical notation in an object-oriented way
* Generate and transform musical materials
* Construct powerful component selectors and iterators for modifying musical objects in a score
* Control all of the typographic details of music notation
* Play musical scores through scsynth


## <br>Installation

### Install LilyPond

[LilyPond][lilypond] is an open-source program that engraves music notation in an automated way. __Fosc__ uses LilyPond to produce notational output. It's recommended that you install the most recent version of LilyPond directly from the LilyPond website.

### <br>Install Fosc

Download the __fosc__ master branch and unzip. Rename the 'fosc-master' folder to 'fosc'. Move the 'fosc' folder to your SuperCollider Extensions directory. Information on installing SuperCollider extensions can be found here: https://doc.sccode.org/Guides/UsingExtensions.html. 


### <br>Configure Fosc

In your sclang startup file, add code to allow __Fosc__ to communicate with LilyPond. __Note__: it's possible that your LilyPond executable is installed somewhere different to the standard locations below.

__Mac OS X__
```supercollider
Fosc.lilypondPath = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond";
```

__Linux__
```supercollider
Fosc.lilypondPath = "/usr/local/bin/lilypond";
```

<br>Once you've saved your changes, recompile the SuperCollider class library and test that __Fosc__ is able to call LilyPond.

```supercollider
Fosc.lilypondVersion;
```
```
2.19.82
```


## <br>Creating score objects

<br>Display a note.
```supercollider
a = FoscNote(60, 1/4);
a.show;
```
<!-- ![](./docs/img/displaying-music-1.png) -->
![](./docs/img/README-1.svg)


<br>Display some notes in sequence.
```supercollider
a = FoscVoice([FoscNote(60, 1/4), FoscNote(62, 1/8)]);
a.show;
```
<!-- ![](./docs/img/displaying-music-2.png) -->
![](./docs/img/README-2.svg)

<br>View LilyPond output in the Post window.
```supercollider
a = FoscVoice([FoscNote(60, 1/4), FoscNote(62, 1/8)]);
a.lilypond;
```
```
\new Voice {
    c'4
    d'8
}
```

<br>Display a score.
```supercollider
a = FoscVoice([FoscNote("c'", 1/4), FoscNote("d'", 1/8)]);
b = FoscVoice([FoscNote("bf", 1/8), FoscNote("af", 1/4)]);
c = FoscScore([FoscStaff([a]), FoscStaff([b])]);
c.show;
```
<!-- ![](./docs/img/displaying-music-3.png) -->
![](./docs/img/README-3.svg)


## <br>Augmenting and tweaking score objects

<br>Indicators attach to leaves.
```supercollider
a = FoscNote(60, 1/4);
a.attach(FoscArticulation('>'));
a.attach(FoscDynamic('f'));
a.show;
```
<!-- ![](./docs/img/indicators-1.png) -->
![](./docs/img/README-4.svg)

<br>Spanners attach to two or more contiguous leaves.
```supercollider
a = FoscStaff(#[60,62,64,65].collect { |pitch| FoscNote(pitch, 1/8) });
a.selectLeaves.slur;
a.selectLeaves.hairpin('p < f');
a.show;
```
<!-- ![](./docs/img/spanners-1.png) -->
![](./docs/img/README-5.svg)

<br>Override LilyPond Grob properties.
```supercollider
a = FoscNote(60, 1/4);
override(a).noteHead.style = 'harmonic';
a.show;
```
<!-- ![](./docs/img/overrides-settings-1.png) -->
![](./docs/img/README-6.svg)

<br>Set LilyPond Context properties.
```supercollider
a = FoscScore([FoscStaff(#[60,62,64,65].collect { |pitch| FoscNote(pitch, 1/8) })]);
set(a[0]).instrumentName = FoscMarkup("Violin");
set(a).proportionalNotationDuration = FoscSchemeMoment(#[1,64]);
a.show;
```
<!-- ![](./docs/img/overrides-settings-2.png) -->
![](./docs/img/README-7.svg)


## <br>Creating music with FoscMusicMaker

<br>Make music from *durations* and *pitches*.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/4 ! 4, pitches: #[60,62,64,65]);
b.show;
```
<!-- ![](./docs/img/music-maker-1.png) -->
![](./docs/img/README-8.svg)

<br>Use a string of LilyPond note names.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/4 ! 4, pitches: "c' d' ef' f'");
b.show;
```
<!-- ![](./docs/img/music-maker-2.png) -->
![](./docs/img/README-9.svg)

<br>Pitches are repeated cyclically when the length of *pitches* is less than the length of *durations*.
```supercollider
a = FoscMusicMaker();
b = a.(durations: [3/8,1/8,2/8,2/8], pitches: #[60,62]);
b.show;
```
<!-- ![](./docs/img/music-maker-3.png) -->
![](./docs/img/README-10.svg)

<br>*divisions* embed into *durations* as rhythmic proportions.
```supercollider
a = FoscMusicMaker();
b = a.(durations: [2/16,3/16,5/16], divisions: #[[3,1],[3,2],[4,3]]);
b.show;
```
<!-- ![](./docs/img/music-maker-4.png) -->
![](./docs/img/README-11.svg)

<br>Negative *divisions* are rests.
```supercollider
a = FoscMusicMaker();
b = a.(durations: [2/16,3/16,5/16], divisions: #[[-3,1],[3,2],[4,-3]]);
b.show;
```
<!-- ![](./docs/img/music-maker-5.png) -->
![](./docs/img/README-12.svg)

<br>Rhythm cells are repeated cyclically when the length of *durations* is less than the length of *divisions*.
```supercollider
a = FoscMusicMaker();
b = a.(durations: [1/4], divisions: #[1,1,1,1,1] ! 4);
b.show;
```
<!-- ![](./docs/img/music-maker-6.png) -->
![](./docs/img/README-13.svg)

<br>Rhythm cells are repeated cyclically when the length of *divisions* is less than the length of *durations*.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/4 ! 4, divisions: #[[-1,2,2],[4,-1]]);
b.show;
```
<!-- ![](./docs/img/music-maker-7.png) -->
![](./docs/img/README-14.svg)

<br>Apply a *mask* to fuse contiguous musical events. Mask patterns repeat cyclically. *pitches* are added after a *mask* is applied.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,1], pitches: #[60,62]);
b.show;
```
<!-- ![](./docs/img/music-maker-8.png) -->
![](./docs/img/README-15.svg)

<br>Negative *mask* values are rests.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,-1], pitches: #[60,62]);
b.show;
```
<!-- ![](./docs/img/music-maker-9.png) -->
![](./docs/img/README-16.svg)


## <br>Selections

<br>Select all score components.
```supercollider
a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
b = a.selectComponents;
b.do { |each| each.cs.postln };
```
```
FoscStaff([  ], Staff, false, nil)
FoscRest(1/4)
FoscNote("c'", 1/4)
FoscNote("d'", 1/4)
```

<br>Select notes and rests.
```supercollider
a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
b = a.selectComponents(type: [FoscNote, FoscRest]);
b.do { |each| each.cs.postln };
```
```
FoscRest(1/4)
FoscNote("c'", 1/4)
FoscNote("d'", 1/4)
```

<br>Select pitched leaves.
```supercollider
a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscChord(#[60,64,67], 1/4)]);
b = a.selectLeaves(pitched: true);
b.do { |each| each.cs.postln };
```
```
FoscNote("c'", 1/4)
FoscChord("c' e' g'", 1/4)
```


<br>Attach indicators to all leaves in a selection.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/8 ! 8, pitches: (60..67));
c = FoscStaff(b);
c.selectLeaves(pitched: true).do { |each| each.attach(FoscArticulation('>')) };
c.show;
```
<!-- ![](./docs/img/selections-1.png) -->
![](./docs/img/README-17.svg)


## <br>Iteration

<br>Iterate over all score components.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/8 ! 8, pitches: (60..67));
c = FoscStaff(b);
c.doComponents({ |each| each.cs.postln });
```
```
FoscStaff([  ], Staff, false, nil)
FoscNote("c'", 1/8)
FoscNote("cs'", 1/8)
FoscNote("d'", 1/8)
FoscNote("ef'", 1/8)
FoscNote("e'", 1/8)
FoscNote("f'", 1/8)
FoscNote("fs'", 1/8)
FoscNote("g'", 1/8)
```


<br>Iterate over notes, attaching indicators.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/8 ! 4, mask: #[-1,1,1,1], pitches: #[60,61,62]);
c = FoscStaff(b);
c.doComponents({ |note| note.attach(FoscArticulation('>')) }, type: FoscNote);
c.show;
```
<!-- ![](./docs/img/iteration-1.png) -->
![](./docs/img/README-18.svg)


<br>Iterate over runs, attaching slurs.
```supercollider
a = FoscMusicMaker();
b = a.(durations: 1/8 ! 8, mask: #[-1,1,1,1,-1,1,1,1], pitches: (60..65));
c = FoscStaff(b);
c.doRuns { |run| if (run.size > 1) { run.slur } };
c.show;
```
<!-- ![](./docs/img/iteration-2.png) -->
![](./docs/img/README-19.svg)


## <br>License
__Fosc__ is free software available under [Version 3.0 of the GNU General Public License](./LICENSE).

