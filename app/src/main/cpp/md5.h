typedef unsigned char *POINTER;
typedef unsigned short int UINT2;
typedef unsigned long int UINT4;
typedef struct tagMD5_CTX{
    UINT4 state[4];
    UINT4 count[2];
    unsigned char buffer[64];
} MD5_CTX;
#ifdef __cplusplus
extern "C" {
#endif
void MD5Init (MD5_CTX *context);
void MD5Update (MD5_CTX *context,unsigned char *input,unsigned int inputLen);
void MD5Final (unsigned char digest[16], MD5_CTX *context);

static void MD5Transform (UINT4 state[4], unsigned char block[64]);
static void Encode (unsigned char *output, UINT4 *input, unsigned int len);
static void Decode (UINT4 *output, unsigned char *input, unsigned int len);
static void MD5_memcpy (POINTER output, POINTER input, unsigned int len);
static void MD5_memset (POINTER output, int value, unsigned int len);

#ifdef __cplusplus
}
#endif
