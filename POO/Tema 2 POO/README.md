Tema 2 POO: 

Note: Tema consta in creearea unui sistem care sa simuleze structura unei primarii.
Tema presupune creearea unei structuri care se mentina intr-o baza de date toti utilizatorii si cererile acestora la primarie.
Utilizatorii pot fii de 5 feluri : Persoana, Pensionar, Elev, Entitate Juridica, Angajat, fiecare avand restrictii in cererile pe care le pot depune.
Cererile pot fi create, rezolvate, retrase sau afisate.
Primaria contine 5 birouri, create prin genericitate, pentru fiecare tip de utilizator.
Birourile, la randurile lor contin o colectie care pastreaza functionarii publici .
Functionarii publici sunt o clasa separata, creata de asemenea prin genericitate, responsabila de rezolvarea cererilor tipului de utilizator asignat prin genericitate
Pentru verificare, tema presupune citirea din fisierul de input, si scrierea intr-un fisier de output.
Pentru acest lucru, am creeat o clasa speciala numita FileOperations, ce se ocupa exclusiv de acest lucru.
