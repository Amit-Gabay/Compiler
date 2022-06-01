# 🐉 L compiler 🐉
L programming language java-based compiler for Linux🐧, as part of a project in compilation course at Tel-Aviv University 🎓.
L is an object-oriented programming language (OOP), which is a simplified version of java.

As part of the compilation process, the L code being translated into an Intermediate Representation code (IR code),
which eventually being translated into MIPS assembly.


### Usage
In order to create (compile🤯) the compiler, use `make compile`;
To create and execute the compiled MIPS assembly code, use `make everything`.
After creating the compiler, you can compile L source code files using:
```
$ COMPILER <source-code-path> <output-MIPS-path>
```


### L syntax grammar
![image](https://user-images.githubusercontent.com/68384440/171468422-35613a18-c329-43da-9300-217209aa7875.png)
