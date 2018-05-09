package com.main;

import android.util.Log;

/**
 *
 */

public class MAP27 {
    private static String TAG = "FrameAdapter";
    private static boolean D = false;

    private static final int MAX_DATA = 1000;

    private static final byte SYN = 0x16;
    private static final byte DLE = 0x10;
    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;

    private  byte[] data;
    private  int dataIndex = 0;
    int state = 0;

    int fcs = 0;

    static int crc16 = 0xA001;

    // Table for fcs calculation
    static int[] mtab = {0x0000, 0xc1c0, 0x81c1, 0x4001, 0x01c3, 0xc003, 0x8002, 0x41c2, 0x01c6, 0xc006, 0x8007, 0x41c7, 0x0005, 0xc1c5, 0x81c4, 0x4004,
            0x01cc, 0xc00c, 0x800d, 0x41cd, 0x000f, 0xc1cf, 0x81ce, 0x400e, 0x000a, 0xc1ca, 0x81cb, 0x400b, 0x01c9, 0xc009, 0x8008, 0x41c8,
            0x01d8, 0xc018, 0x8019, 0x41d9, 0x001b, 0xc1db, 0x81da, 0x401a, 0x001e, 0xc1de, 0x81df, 0x401f, 0x01dd, 0xc01d, 0x801c, 0x41dc,
            0x0014, 0xc1d4, 0x81d5, 0x4015, 0x01d7, 0xc017, 0x8016, 0x41d6, 0x01d2, 0xc012, 0x8013, 0x41d3, 0x0011, 0xc1d1, 0x81d0, 0x4010,
            0x01f0, 0xc030, 0x8031, 0x41f1, 0x0033, 0xc1f3, 0x81f2, 0x4032, 0x0036, 0xc1f6, 0x81f7, 0x4037, 0x01f5, 0xc035, 0x8034, 0x41f4,
            0x003c, 0xc1fc, 0x81fd, 0x403d, 0x01ff, 0xc03f, 0x803e, 0x41fe, 0x01fa, 0xc03a, 0x803b, 0x41fb, 0x0039, 0xc1f9, 0x81f8, 0x4038,
            0x0028, 0xc1e8, 0x81e9, 0x4029, 0x01eb, 0xc02b, 0x802a, 0x41ea, 0x01ee, 0xc02e, 0x802f, 0x41ef, 0x002d, 0xc1ed, 0x81ec, 0x402c,
            0x01e4, 0xc024, 0x8025, 0x41e5, 0x0027, 0xc1e7, 0x81e6, 0x4026, 0x0022, 0xc1e2, 0x81e3, 0x4023, 0x01e1, 0xc021, 0x8020, 0x41e0,
            0x01a0, 0xc060, 0x8061, 0x41a1, 0x0063, 0xc1a3, 0x81a2, 0x4062, 0x0066, 0xc1a6, 0x81a7, 0x4067, 0x01a5, 0xc065, 0x8064, 0x41a4,
            0x006c, 0xc1ac, 0x81ad, 0x406d, 0x01af, 0xc06f, 0x806e, 0x41ae, 0x01aa, 0xc06a, 0x806b, 0x41ab, 0x0069, 0xc1a9, 0x81a8, 0x4068,
            0x0078, 0xc1b8, 0x81b9, 0x4079, 0x01bb, 0xc07b, 0x807a, 0x41ba, 0x01be, 0xc07e, 0x807f, 0x41bf, 0x007d, 0xc1bd, 0x81bc, 0x407c,
            0x01b4, 0xc074, 0x8075, 0x41b5, 0x0077, 0xc1b7, 0x81b6, 0x4076, 0x0072, 0xc1b2, 0x81b3, 0x4073, 0x01b1, 0xc071, 0x8070, 0x41b0,
            0x0050, 0xc190, 0x8191, 0x4051, 0x0193, 0xc053, 0x8052, 0x4192, 0x0196, 0xc056, 0x8057, 0x4197, 0x0055, 0xc195, 0x8194, 0x4054,
            0x019c, 0xc05c, 0x805d, 0x419d, 0x005f, 0xc19f, 0x819e, 0x405e, 0x005a, 0xc19a, 0x819b, 0x405b, 0x0199, 0xc059, 0x8058, 0x4198,
            0x0188, 0xc048, 0x8049, 0x4189, 0x004b, 0xc18b, 0x818a, 0x404a, 0x004e, 0xc18e, 0x818f, 0x404f, 0x018d, 0xc04d, 0x804c, 0x418c,
            0x0044, 0xc184, 0x8185, 0x4045, 0x0187, 0xc047, 0x8046, 0x4186, 0x0182, 0xc042, 0x8043, 0x4183, 0x0041, 0xc181, 0x8180, 0x4040};


    private void trace(String msg)
    {
        if( D ) Log.d( TAG , msg );
    }

    public MAP27() {

        data = new byte[MAX_DATA];
    }

    public int input(byte b)
    {

        int newState = -1;
        if( dataIndex == MAX_DATA)
        {
            reset();
        }

        switch( state )
        {
            case 0:
                dataIndex = 0;
                if( ( b & 0xFF) == SYN ) //SYN
                {
                    newState = 1;
                }
                else
                {
                    newState = -1;
                }
                break;

            case 1:
                if( ( b & 0xFF) == DLE ) //DLE
                {
                    newState = 2;
                }
                else
                {
                    newState = -1;
                }
                break;

            case 2:
                if( ( b & 0xFF) == STX ) //STX
                {
                    newState = 3;
                }
                else
                {
                    newState = -1;
                }
                break;

            case 3:
                if( ( b & 0xFF) == DLE ) //DLE
                {
                    newState = 4;
                }
                else
                {
                    newState = 3;
                    data[ dataIndex ] = b;
                    dataIndex++;
                }
                break;

            case 4:
                if( ( b & 0xFF) == DLE ) //DLE
                {
                    newState = 3;
                    data[ dataIndex ] = b;
                    dataIndex++;
                }
                else
                {
                    if( ( b & 0xFF) == ETX ) //ETX
                    {
                        newState = 5;
                        data[ dataIndex ] = DLE;// DLE;
                        dataIndex++;
                        data[ dataIndex ] = ETX;// ETX
                        dataIndex++;

                        fcs = calculateFcs( data , dataIndex );
                        trace("fcs:" + ((fcs>>8)&0xFF ) + ":" + (fcs&0xff) );
                        //fcs = 0; // calculate checksum /////////////////
						/*
						int[] tt = new int[256];
				        create_table(tt);
						fcs = fcs_calc(tt,data,dataIndex);*/
                    }
                    else
                    {
                        newState = -1;
                    }
                }
                break;

            case 5:
                if( fcs>>8 == (b&0xFF ))
                {
                    newState = 6;
                    data[ dataIndex ] = b;
                    dataIndex++;
                }
                else
                {
                    newState = -1;
                }
                break;

            case 6:
                if( (fcs&0xFF) == (b&0xFF) )
                {
                    newState = 7;
                    data[ dataIndex ] = b;
                    dataIndex++;

                    //Log.d("crc", "FRAME ACCEPTED" );

                }
                else
                {
                    newState = -1;
                }
                break;

            case 7:
                newState = 7;
                break;
        }

//        Log.d("framadapter","dataindex" + dataIndex);
        if( newState == -1)
        {
            reset();
        }
        else
        {
            state = newState;
        }

        return state;
    }

    public void reset()
    {
        dataIndex = 0;
        state = 0;
    }

    public byte[] getData()
    {
        return data;
    }

    public int getState()
    {
        return state;
    }

    public static byte[] adapt( byte[] data )
    {
        byte[] frame = new byte[MAX_DATA];
        int frameI; //iterators
        int lengthF; // length in Lenght Field

        // insert begin delimiter
        frame[0] = SYN; //SYN
        frame[1] = DLE; //DLE
        frame[2] = STX; //STX

        // obtain Length Field
        lengthF = ( data[0] & 0xFF ) << 8;
        lengthF += data[1] & 0xFF;

        frameI = 3;

        for( int i = 0 ; i < lengthF+2 ; i++ )
        {
            // protect DLE characters
            if( data[i] == DLE /*DLE*/ )
            {
                frame[ frameI ] = DLE;
                frameI++;
            }

            frame[ frameI ] = data[i];

            frameI++;
        }

        // insert end delimiter
        frame[ frameI ] = DLE;		// DLE
        frame[ frameI + 1 ] = ETX; // ETX

        // calculate checksum
        //int fcs = calculateFcs( frame , frameI+2 );


        int fcs = 0; // calculate checksum /////////////////
        int[] tt = new int[256];
        create_table(tt);
        fcs = fcs_calc(tt,frame,frameI+2);


        // insert checksum
        frame[ frameI + 2 ] = (byte) ( (fcs>>8) & 0xFF);
        frame[ frameI + 3 ] = (byte) ( fcs & 0xFF);
/*
		for(int i= 0; i<frameI+4 ; i++)
		{
			Log.d("fa","ff "+Integer.toHexString(frame[i]&0xff ) );
		}*/
        return frame;
    }



    static void create_table( int[] mtab )
    {
        int[] btab = new int[8];
        int i,j;
        int q;
        int shreg;
        int carry,bit;

        carry = 1;
        shreg = 0;
        for( i=0 ; i<8 ; i++ )
        {
            if(carry != 0)
            {
                shreg^=crc16;
            }
            btab[i] = (shreg<<8)|(shreg>>8);
            carry = shreg&1;
            shreg>>=1;
        }

        for (i=0; i<256; i++)
        {
            q=0;
            bit=0x80;
            for (j=0; j<8; j++)
            {
                if ((bit & i)!=0)
                {
                    q^=btab[j];
                }
                bit>>=1;
            }
            mtab[i]=q&0xFFFF;
        }
    }

    static int fcs_calc( int[] mtab, byte[] buff , int len )
    {
        int fcs;
        int q;

        fcs = 0xFFFF;

        for( int i = 3; i<len ; i++ )
        {
            q = mtab[   (  (fcs&0xFFFF) >> 8  ) ^ (buff[i]&0xFF)  ];

            fcs = ( ((q>>8)^(fcs%256))<<8) + q%256;
            //fcs = ~fcs & 0xFFFF;
        }
        return ~fcs & 0xFFFF;
    }

    public boolean isFullFrame()
    {
        return (state==7);
    }

    static int calculateFcs( byte[] buff , int len )
    {
        int fcs;
        int q;

        fcs = 0xFFFF;

        for( int i = 0; i<len ; i++ )
        {
            q = mtab[   (  (fcs&0xFFFF) >> 8  ) ^ (buff[i]&0xFF)  ];

            fcs = ( ((q>>8)^(fcs%256))<<8) + q%256;
            //fcs = ~fcs & 0xFFFF;
        }
        return ~fcs & 0xFFFF;
    }
}