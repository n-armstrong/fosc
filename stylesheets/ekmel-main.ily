%% This file is part of Ekmelily - Notation of microtonal music with LilyPond.
%% Copyright (C) 2013-2021  Thomas Richter <thomas-richter@aon.at>
%%
%% This program is free software: you can redistribute it and/or modify
%% it under the terms of the GNU General Public License as published by
%% the Free Software Foundation, either version 3 of the License, or
%% (at your option) any later version.
%%
%% This program is distributed in the hope that it will be useful,
%% but WITHOUT ANY WARRANTY; without even the implied warranty of
%% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
%% GNU General Public License at <http://www.gnu.org/licenses/>
%% for more details.
%%
%%
%% File: ekmel-main.ily  -  Main include file
%% Version: 3.7
%% Latest revision: 1 August 2021
%%

\version "2.19.22"


%% Constants

% Enharmonically equivalent accidentals
#(define EKM-ACODE-EQUIV #x0200)
#(define EKM-ALTER-EQUIV 1/1024)

% Mask to ignore enh. equivalence (EKM-ACODE-EQUIV) and direction (bit 0)
#(define EKM-ACODE-MASK (lognot #x0201))

% Left/Right parenthesis for cautionary accidentals
#(define EKM-LPAR (list (ly:wide-char->utf-8 #xE26A)))
#(define EKM-RPAR (list (ly:wide-char->utf-8 #xE26B)))


%% Auxiliary functions for enh. equivalence

#(define (ekm:equiv? a)
  ;; Return #t if the alteration @var{a} is enh. equivalent.
  (>= (denominator a) 1024))

#(define (ekm:real-alteration a)
  ;; Return the real (correct) value of the alteration @var{a}.
  (if (ekm:equiv? a)
    ((if (negative? a) + -) a EKM-ALTER-EQUIV)
    a))

#(define (ekm:real-pitch p)
  ;; Return the pitch @var{p} with its real (correct) alteration (not used here).
  (let ((a (ly:pitch-alteration p)))
    (if (ekm:equiv? a)
      (ly:make-pitch
        (ly:pitch-octave p)
        (ly:pitch-notename p)
        ((if (negative? a) + -) a EKM-ALTER-EQUIV))
      p)))


%% Tuning

#(define (ekm:tuning? c)
  ;; Return #t if the alteration code @var{c} is defined in the tuning table.
  (or (= 0 c) (pair? (assv (logand c EKM-ACODE-MASK) ekmTuning))))

#(define (ekm:code->alter c)
  ;; Return the alteration for the alteration code @var{c} (must be defined).
  (let* ((v (if (= 0 c) 0 (assv-ref ekmTuning (logand c EKM-ACODE-MASK))))
         (a (if (logtest c EKM-ACODE-EQUIV) (+ v EKM-ALTER-EQUIV) v)))
    (if (logtest c 1) (- a) a)))


%% Language (pitch names)

% List of alteration codes occurring in the language table
#(define ekm:acodes #f)

#(define (ekm:set-language lang)
  ;; Install the language table of the name @var{lang}
  ;; and set the pitchnames table.
  (let* ((ac '())
         (src (or (assq-ref ekmLanguages lang) (cdar ekmLanguages)))
         (tab (map
                (lambda (e)
                  (if (not (memv (cddr e) ac))
                    (set! ac (cons (cddr e) ac)))
                  (cons
                    (car e)
                    (ly:make-pitch
                      -1 (cadr e) (ekm:code->alter (cddr e)))))
                (filter
                  (lambda (e) (ekm:tuning? (cddr e)))
                  (if (symbol? src) (assq-ref ekmLanguages src) src)))))
    (set! ekm:acodes (cons -1 ac)) ;; dummy element for delv! in ekm:set-notation
    (set! pitchnames tab)
    (ly:parser-set-note-names tab)))


%% Notation (style)

#(define ekm:notation-name "")
#(define ekm:notation '())

#(define (ekm:acc->string ls)
  ;; Return a list with all ACC-ELEMENTs in the list @var{ls} as strings.
  (map
    (lambda (e)
      (cond
        ((integer? e) (ly:wide-char->utf-8 e))
        ((char? e) (ly:wide-char->utf-8 (char->integer e)))
        ((string? e) e)
        (else "")))
    ls))

#(define (ekm:set-notation name)
  ;; Install the notation table of the name @var{name}.
  (let* ((ac  (list-copy ekm:acodes))
         (src (or
                (assq-ref ekmNotations (string->symbol name))
                (ly:error "Unknown notation `~a'." name)))
         (tab (map
                (lambda (e)
                  (delv! (car e) ac)
                  (cons*
                    (ekm:code->alter (car e))
                    (ekm:acc->string (cdr e))))
                (filter
                  (lambda (e) (ekm:tuning? (car e))) ;; or (lambda (e) (memv (car e) ac))
                  src)))
         (rem (map
                (lambda (e)
                  (let ((enh #f))
                    (if (logtest e EKM-ACODE-EQUIV)
                      (begin
                        (set! enh (assv-ref src (logxor e EKM-ACODE-EQUIV)))
                        (if (not enh)
                          (ly:warning "Missing accidental for enh. equivalent alteration code `~a'."
                            (format #f "0x~:@(~x~)" e))))
                      (ly:warning "Missing accidental for alteration code `~a'."
                        (format #f "0x~:@(~x~)" e)))
                    (cons*
                      (ekm:code->alter e)
                      (ekm:acc->string (or enh '(#f))))))
                (cdr ac)))) ;; missing alteration codes ignoring dummy element
    (set! ekm:notation-name name)
    (set! ekm:notation (append tab rem))))


%% Font

#(define ekm:font-name
  (let ((font (ly:get-option 'ekmelic-font)))
    (if font
      (symbol->string font)
      (if (and (defined? 'ekmelicFont) (string? ekmelicFont))
        ekmelicFont
        "Ekmelos"))))

#(define ekm:font-size 5)


%% Main procs (stencils)

#(define-markup-command (ekmelic-acc layout props alt rst par)
  ;; Create the accidental of the alteration @var{alt} (must be defined),
  ;; with a natural if @var{rst} is true, or parenthesized if @var{par} is true.
  (rational? boolean? boolean?)
  (let* ((str (assv-ref ekm:notation alt))
         (acc '()))
    (for-each
      (lambda (s)
        (set! acc
          (ly:stencil-combine-at-edge
            acc
            X RIGHT
            (interpret-markup layout props
              (markup
                #:fontsize ekm:font-size
                #:override `(font-name . ,ekm:font-name)
                s))
            0.12)))
      (cond
        (rst (append (assv-ref ekm:notation 0) str))
        (par (append EKM-LPAR str EKM-RPAR))
        (else str)))
    acc))

#(define* (ekm:acc grob #:optional (par #f))
  ;; Stencil for [TrillPitch|Ambitus]Accidental
  ;; and for AccidentalCautionary if @var{par} is true.
  ;; Note: The property 'parenthesized semms to be always #t and so
  ;; cannot be used to determine a cautionary style.
  (grob-interpret-markup grob
    (markup
      #:ekmelic-acc
        (ly:grob-property grob 'alteration)
        (not (ly:grob-property grob 'restore-first))
        par)))

#(define* (ekm:key grob #:optional (cancel #f))
  ;; Stencil for KeySignature and for KeyCancellation if @var{cancel} is true.
  (let* ((alt (ly:grob-property grob 'alteration-alist))
         (c0  (ly:grob-property grob 'c0-position))
         (sig '()))
    (for-each
      (lambda (e)
        (set! sig
          (ly:stencil-combine-at-edge ;; or ly:stencil-stack ?
            (grob-interpret-markup grob
              (markup
                #:raise
                  (/ (car (key-signature-interface::alteration-positions e c0 grob)) 2)
                  #:ekmelic-acc (if cancel 0 (cdr e)) #f #f))
            X RIGHT
            sig
            0.16))) ;; 0.04em
      alt)
    sig))

% #(define (ekm:alt->string alt)
%   (number->string (if (>= (denominator alt) 256) (- alt EKM-ALTER-EQUIV) alt)))


%% Aux procs for ekmelicUserStyle

#(define (ekm:list-prefix pfx ls)
  (cond
    ((null? pfx) ls)
    ((null? ls) #f)
    ((equal? (car pfx) (car ls))
      (ekm:list-prefix (cdr pfx) (cdr ls)))
    (else #f)))

#(define (ekm:list-replace! old new ls)
  (if (null? ls)
    '()
    (let ((tl (ekm:list-prefix old ls)))
      (if tl
        (append
          new
          (ekm:list-replace! old new tl))
        (begin
          (set-cdr! ls (ekm:list-replace! old new (cdr ls)))
          ls)))))


%% Main settings

language =
#(define-void-function (lang)
  (string?)
  (ekm:set-language (string->symbol lang)))

ekmelicStyle =
#(define-void-function (name)
  (string?)
  (ekm:set-notation name))

ekmelicUserStyle =
#(define-void-function (name src)
  (string? list?)
  (set! ekm:notation-name
    (if (string-null? name) (string-append ekm:notation-name "-user") name))
  (for-each
    (lambda (u)
      (if (and (pair? u) (not (null? (cdr u))))
        (let ((old (assv-ref ekm:notation (car u)))
              (new (ekm:acc->string (cdr u))))
          (if old
            (for-each
              (lambda (e)
                (set-cdr! e (ekm:list-replace! old new (cdr e))))
              ekm:notation)))))
    src))

ekmelicOutputSuffix =
#(define-void-function () ()
  (set! (paper-variable #f 'output-suffix) ekm:notation-name))


%% Markup commands

#(define-markup-command (ekmelic-style-name layout props)
  ()
  (interpret-markup layout props
    ekm:notation-name))

#(define-markup-command (ekmelic-char layout props alt)
  (rational?)
  #:properties ((font-size 1))
  (interpret-markup layout props
    (markup
      #:override `(font-size . ,(- font-size 3))
      #:ekmelic-acc alt #f #f)))

#(define-markup-command (ekmelic-fraction layout props alt)
  (rational?)
  (let* ((a (abs (ekm:real-alteration alt)))
         (n (number->string (numerator a)))
         (f (if (= 1 (denominator a))
              n (make-fraction-markup n (number->string (denominator a))))))
    (interpret-markup layout props
      (if (negative? alt)
        (make-concat-markup
          (list (string-append "-" (ly:wide-char->utf-8 #x2009)) f))
        f))))


%% Initializations

#(begin
  ;; global default scale
  (let ((s (assv-ref ekmTuning -1)))
    (ly:set-default-scale
      (ly:make-scale
        (if s (list->vector s) #(0 1 2 5/2 7/2 9/2 11/2)))))

  ;; guess current language set by LilyPond's command
  (ekm:set-language (if (assq 'cff pitchnames) 'english 'deutsch))

  ;; notation table
  (ekm:set-notation (symbol->string
    (or (ly:get-option 'ekmelic-style) (caar ekmNotations)))))


\layout {
  \context {
    \Score
    \override Accidental.stencil = #ekm:acc
    \override Accidental.horizontal-skylines = #'()
    \override Accidental.vertical-skylines = #'()
    \override AccidentalCautionary.stencil = #(lambda (grob) (ekm:acc grob #t))
    \override KeySignature.stencil = #ekm:key
    \override KeyCancellation.stencil = #(lambda (grob) (ekm:key grob #t))
    \override TrillPitchAccidental.stencil = #ekm:acc
    \override AmbitusAccidental.stencil = #ekm:acc
  }
}
