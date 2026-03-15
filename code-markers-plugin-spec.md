# Code Markers – IntelliJ Plugin Specification

## Goal

Create a JetBrains IntelliJ Platform plugin named **Code Markers**.

The plugin adds deterministic visual identity markers to PHP classes and methods in the editor using:

- color stripes
- class emoji markers

The purpose is visual navigation and quick recognition of code structure.

---

# Plugin Metadata

Plugin name:

Code Markers

Description:

Rainbow-style visual identity for classes and methods.

Supported IDE:

PhpStorm

Language:

Kotlin

Build system:

Gradle (Kotlin DSL)

Plugin SDK:

IntelliJ Platform Plugin SDK

Required dependency:

com.jetbrains.php

---

# Core Functionality

The plugin adds visual markers to:

- classes
- interfaces
- traits
- enums
- methods

Anonymous classes are ignored.

Top-level functions are ignored.

---

# Visual Markers

## Class

Each class receives:

- class emoji
- class color stripe

Example:

|blue| 🍎 class OrderService

Stripe parameters:

width = 3px  
gap = 2px

The class stripe appears only on the class declaration line.

---

## Method

Each method receives:

- inherited class emoji
- class color stripe
- method color stripe

Example:

|blue| gap |green| 🍎 function createOrder()

Rules:

- method stripe spans the entire method block
- appears on every line inside the method
- includes empty lines
- ends at the closing brace

Example visualization:

|blue| gap |green| function createOrder()  
|blue| gap |green| {  
|blue| gap |green|     doSomething();  
|blue| gap |green|  
|blue| gap |green| }

---

# Constructor Special Rule

Constructors inherit the class color.

Example:

|blue| gap |blue| function __construct()

Constructor does not receive a method hash color.

---

# Color System

Colors are selected from a fixed palette of 12 colors.

Example palette:

blue  
green  
orange  
purple  
red  
cyan  
yellow  
pink  
teal  
indigo  
lime  
amber

Method color cannot equal class color.

If collision occurs:

methodColorIndex = (methodColorIndex + 1) % paletteSize

---

# Emoji System

Emoji are assigned per class only.

Methods inherit the class emoji.

Emoji set size:

24 emojis

Example set:

🍎 🍋 🍇 🥝 🍍 🥥  
🍑 🍒 🫐 🍉 🍌 🍐  
⚽ 🎯 🧩 🎲  
🔧 🪛 🔩 ⚙️  
🐢 🦊 🐙

Emoji selection:

emojiIndex = hash(FQCN) % emojiSetSize

---

# Hashing

Use MurmurHash3.

Class hash input:

FQCN

Example:

App\Service\OrderService

Method hash input:

FQCN::methodName

Example:

App\Service\OrderService::createOrder

If a class has no namespace:

filePath::className

Example:

src/Service/Foo.php::Foo

---

# Rendering Rules

Stripe position:

fixed X position

Stripe does not follow indentation.

Example:

|blue| gap |green| function foo()  
|blue| gap |green| {  
|blue| gap |green|     if (...) {  
|blue| gap |green|         bar();  
|blue| gap |green|     }  
|blue| gap |green| }

---

# PSI Elements to Support

Supported:

PhpClass  
PhpInterface  
PhpTrait  
PhpEnum  
Method

Ignored:

Anonymous classes  
Top-level functions  
Enum cases

---

# Settings UI

Add a plugin settings page with the following options:

Enable visual markers

Show class emoji

Show method stripe

Palette:
- Default
- Pastel
- High Contrast

Palette selection changes only color mapping, not hashes.

---

# Architecture

Implement using the following IntelliJ extension points.

### LineMarkerProvider

Responsible for:

- class stripe
- method stripe
- method block highlighting

---

### InlayHintsProvider

Responsible for:

- rendering emoji before class and method names.

---

### PsiTreeChangeListener

Responsible for:

- cache invalidation
- recalculating class and method hashes

Triggers:

- class rename
- method rename
- namespace change
- method add/remove

Cache scope:

file-level cache

---

# Expected Project Structure

code-markers
├─ build.gradle.kts
├─ settings.gradle.kts
├─ src/main/kotlin/
│   ├─ markers/
│   │   ├─ CodeMarkerLineProvider.kt
│   │   ├─ EmojiInlayProvider.kt
│   │   ├─ MarkerHashService.kt
│   │   ├─ PaletteProvider.kt
│   │   └─ PsiCacheService.kt
│
├─ src/main/resources/
│   └─ META-INF/plugin.xml

---

# Requirements for the Generated Project

The project should include:

1. Gradle IntelliJ plugin setup
2. Kotlin plugin code
3. PhpStorm dependency
4. Plugin XML configuration
5. Basic implementations of:

LineMarkerProvider  
InlayHintsProvider  
SettingsConfigurable  
PsiTreeChangeListener

The code does not need to be fully production ready, but it should compile and run inside a sandbox IDE.

---

# Deliverables

Generate:

1. Full Gradle IntelliJ plugin project
2. Kotlin source files
3. plugin.xml configuration
4. settings UI
5. minimal working stripe rendering
6. emoji inlay rendering

The project must be ready to open in IntelliJ IDEA and run using the Gradle runIde task.