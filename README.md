# fosc

[lilypond]: http://lilypond.org/
[supercollider]: https://supercollider.github.io/

__fosc__ is a [SuperCollider][supercollider] API for generating musical notation in [LilyPond][lilypond].


## Installation

### 1. Install LilyPond

[LilyPond][lilypond] is an open-source program that engraves music notation in an automated way. __fosc__ uses LilyPond to produce notational output. It's recommended that you install the most recent version of LilyPond directly from the LilyPond website. After you install LilyPond, check to see if LilyPond is callable from your command line.

    ~$ lilypond --version

    GNU LilyPond 2.19.83

    Copyright (c) 1996--2015 by
      Han-Wen Nienhuys <hanwen@xs4all.nl>
      Jan Nieuwenhuizen <janneke@gnu.org>
      and others.

    This program is free software.  It is covered by the GNU General Public
    License and you are welcome to change it and/or distribute copies of it
    under certain conditions.  Invoke as `lilypond --warranty` for more
    information.


### 2. Install fosc
Download the __fosc__ master branch from this repository. Move the unzipped folder to your SuperCollider Extensions directory. Information on installing extensions to the SuperCollider base class library can be found here: https://doc.sccode.org/Guides/UsingExtensions.html. 


### 3. Configure fosc

In your sclang startup file, add code to allow __fosc__ to communicate with LilyPond. __Note__: it's possible that your LilyPond binary may be installed somewhere different to the standard locations below.

####Mac OS X
```supercollider
FoscConfiguration.lilypondExecutablePath = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond";
```

####Linux
```supercollider
FoscConfiguration.lilypondExecutablePath = "/usr/local/bin/lilypond";
```

Once you've saved your changes, recompile the SuperCollider class library and test that __fosc__ is able to call LilyPond.

```supercollider
FoscConfiguration.getLilypondVersionString;

-> 2.19.82
```

