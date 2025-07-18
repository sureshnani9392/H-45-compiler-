; H-45 Compiler Generated Code
; Target: Assembly-like Intermediate Representation

section .text
global _start


main:
    push ebp
    mov ebp, esp
    sub esp, 64     ; reserve space for locals
    ; Block start
    ; Print statement
    mov eax, str_0    ; string literal
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
