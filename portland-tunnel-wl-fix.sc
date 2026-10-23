SCRIPT_START
NOP
{
    LVAR_INT scplayer 
    LVAR_FLOAT ms_aWaterZs 

    main:
    
    GET_PLAYER_CHAR (0) (scplayer) 
    WHILE IS_PC_VERSION
        WAIT (0)
        
        IF LOCATE_CHAR_ANY_MEANS_3D (scplayer, 769.0, 175.0, -18.0, 60.0, 20.0, 50.0, FALSE)

            READ_MEMORY (0x6654AC, 4, FALSE) (ms_aWaterZs)
            WRITE_MEMORY (0x6654AC, 4, -10.0, FALSE)    

            WHILE LOCATE_CHAR_ANY_MEANS_3D (scplayer, 769.0, 175.0, -18.0, 60.0, 20.0, 50.0, FALSE)
                WAIT (0)
            ENDWHILE

            WRITE_MEMORY (0x6654AC, 4, ms_aWaterZs, FALSE)
            
        ENDIF
    ENDWHILE

}
SCRIPT_END