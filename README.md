# Reed-Solomon_error_correction
This app contains [Reed-Solomon](https://en.wikipedia.org/wiki/Reed%E2%80%93Solomon_error_correction) code generator and Reed-Solomon decoder with error correction

Class <b>GaloisField</b> represents [Galois field](https://en.wikipedia.org/wiki/Finite_field). Build field means defining all numbers in it and associate their codes with their powers(method buildField). For this purpose irreducible polynomial of power m must be defined. Therefore amount of number in the field equals 2m - 1. Also there are other operations in <b>GaloisField</b>: summ on number codes, multiplying on powers or codes(that is reduced to mul on powers), dividing on codes. There is method to get power of number using its code and vice versa.

Class <b>RSCodeGenerator</b> represents Reed-Solomon code generator. It takes two parameters: irreducible polynomial and number of symbols, that could be damaged. Based on polynomial Galois field is built. Using field and information of errors number, generating polynomial is built(method makeGenPolynom).

The main function of <b>RSCodeGenerator</b> objects is method encode, that takes information part of the code, appends control symbols to it and returns ready Reed-Solomon code.

Class <b>RSCodeGenerator</b> represents Reed-Solomon decoder. The main function is method decode, that takes code and returns corrected information part.
