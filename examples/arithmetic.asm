; H-45 Compiler Generated Code
; Target: Assembly-like Intermediate Representation

section .text
global _start


main:
    push ebp
    mov ebp, esp
    sub esp, 64     ; reserve space for locals
    ; Block start
    ; Variable declaration: x
    mov eax, 10    ; integer literal
    mov [x], eax    ; store initial value
    ; Variable declaration: y
    mov eax, 20    ; integer literal
    mov [y], eax    ; store initial value
    ; Variable declaration: sum
    mov eax, [x]    ; load variable x
    push eax        ; save left operand
    mov eax, [y]    ; load variable y
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    add eax, ebx    ; addition
    mov [sum], eax    ; store initial value
    ; Variable declaration: difference
    mov eax, [x]    ; load variable x
    push eax        ; save left operand
    mov eax, [y]    ; load variable y
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    sub eax, ebx    ; subtraction
    mov [difference], eax    ; store initial value
    ; Variable declaration: product
    mov eax, [x]    ; load variable x
    push eax        ; save left operand
    mov eax, [y]    ; load variable y
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    imul eax, ebx   ; multiplication
    mov [product], eax    ; store initial value
    ; Variable declaration: quotient
    mov eax, [y]    ; load variable y
    push eax        ; save left operand
    mov eax, [x]    ; load variable x
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    cdq             ; sign extend
    idiv ebx        ; division
    mov [quotient], eax    ; store initial value
    ; Print statement
    mov eax, [sum]    ; load variable sum
    push eax        ; push value to print
    call print_int  ; call print function
    add esp, 4      ; clean up stack
    ; Print statement
    mov eax, [difference]    ; load variable difference
    push eax        ; push value to print
    call print_int  ; call print function
    add esp, 4      ; clean up stack
    ; Print statement
    mov eax, [product]    ; load variable product
    push eax        ; push value to print
    call print_int  ; call print function
    add esp, 4      ; clean up stack
    ; Print statement
    mov eax, [quotient]    ; load variable quotient
    push eax        ; push value to print
    call print_int  ; call print function
    add esp, 4      ; clean up stack
    ; Return statement
    mov eax, 0    ; integer literal
    mov esp, ebp
    pop ebp
    ret
    ; Block end
    mov esp, ebp
    pop ebp
    ret

_start:
    call main
    mov eax, 1      ; sys_exit
    mov ebx, 0      ; exit status
    int 0x80        ; call kernel
