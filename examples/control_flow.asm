; H-45 Compiler Generated Code
; Target: Assembly-like Intermediate Representation

section .text
global _start


factorial:
    push ebp
    mov ebp, esp
    sub esp, 64     ; reserve space for locals
    ; Block start
    ; If statement
    mov eax, [n]    ; load variable n
    push eax        ; save left operand
    mov eax, 1    ; integer literal
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    cmp eax, ebx
    setle al
    movzx eax, al
    cmp eax, 0
    je else_0
    ; Block start
    ; Return statement
    mov eax, 1    ; integer literal
    mov esp, ebp
    pop ebp
    ret
    ; Block end
    jmp endif_1
else_0:
    ; Block start
    ; Return statement
    mov eax, [n]    ; load variable n
    push eax        ; save left operand
    ; Function call: factorial
    mov eax, [n]    ; load variable n
    push eax        ; save left operand
    mov eax, 1    ; integer literal
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    sub eax, ebx    ; subtraction
    push eax        ; push argument 0
    call factorial
    add esp, 4    ; clean up arguments
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    imul eax, ebx   ; multiplication
    mov esp, ebp
    pop ebp
    ret
    ; Block end
endif_1:
    ; Block end
    mov esp, ebp
    pop ebp
    ret

main:
    push ebp
    mov ebp, esp
    sub esp, 64     ; reserve space for locals
    ; Block start
    ; Variable declaration: num
    mov eax, 5    ; integer literal
    mov [num], eax    ; store initial value
    ; Variable declaration: result
    ; Function call: factorial
    mov eax, [num]    ; load variable num
    push eax        ; push argument 0
    call factorial
    add esp, 4    ; clean up arguments
    mov [result], eax    ; store initial value
    ; Print statement
    mov eax, [result]    ; load variable result
    push eax        ; push value to print
    call print_int  ; call print function
    add esp, 4      ; clean up stack
    ; Variable declaration: i
    mov eax, 0    ; integer literal
    mov [i], eax    ; store initial value
    ; While loop
loop_2:
    mov eax, [i]    ; load variable i
    push eax        ; save left operand
    mov eax, 10    ; integer literal
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    cmp eax, ebx
    setl al
    movzx eax, al
    cmp eax, 0
    je endloop_3
    ; Block start
    ; Print statement
    mov eax, [i]    ; load variable i
    push eax        ; push value to print
    call print_int  ; call print function
    add esp, 4      ; clean up stack
    mov eax, [i]    ; load variable i
    push eax        ; save left operand
    mov eax, 1    ; integer literal
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    add eax, ebx    ; addition
    mov [i], eax    ; assign to i
    ; Block end
    jmp loop_2
endloop_3:
    ; For loop
    ; Variable declaration: j
    mov eax, 0    ; integer literal
    mov [j], eax    ; store initial value
forloop_4:
    mov eax, [j]    ; load variable j
    push eax        ; save left operand
    mov eax, 5    ; integer literal
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    cmp eax, ebx
    setl al
    movzx eax, al
    cmp eax, 0
    je endfor_5
    ; Block start
    ; Print statement
    mov eax, [j]    ; load variable j
    push eax        ; push value to print
    call print_int  ; call print function
    add esp, 4      ; clean up stack
    ; Block end
    mov eax, [j]    ; load variable j
    push eax        ; save left operand
    mov eax, 1    ; integer literal
    mov ebx, eax    ; right operand in ebx
    pop eax         ; left operand in eax
    add eax, ebx    ; addition
    mov [j], eax    ; assign to j
    jmp forloop_4
endfor_5:
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
