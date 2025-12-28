#include <jni.h>

#include <arpa/inet.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

#define BUF_SIZE 1024

static int sock = -1;

static char *udpbroadcast_listener(int port)
{
    struct sockaddr_in addr;
    static char buf[BUF_SIZE];

    sock = socket(AF_INET, SOCK_DGRAM, 0); // UDP
    if (sock < 0)
        return "socket error";

    int broadcast = 1;
    setsockopt(sock, SOL_SOCKET, SO_BROADCAST, &broadcast, sizeof(broadcast));

    memset(&addr, 0, sizeof(addr));
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(port);

    if (bind(sock, (struct sockaddr *)&addr, sizeof(addr)) < 0)
    {
        close(sock);
        return "bind error";
    }

    // receive 1 packet TODO: async shi or idk but something that takes stuff
    // and prints at the same time yk??
    while (1)
    {
        int len = recvfrom(sock, buf, BUF_SIZE - 1, 0, NULL, NULL);
        if (len > 0)
        {
            buf[len] = '\0'; // terminate string
            close(sock);
            return buf;
        }
    }
}

JNIEXPORT jstring JNICALL Java_com_jmattaa_udplistener_MainActivity_listenUDP(
    JNIEnv *env, jobject thiz, jint port)
{
    const char *msg = udpbroadcast_listener((int)port);
    return (*env)->NewStringUTF(env, msg);
}

JNIEXPORT void JNICALL
Java_com_jmattaa_udplistener_MainActivity_stopUDP(JNIEnv *env, jobject thiz)
{
    if (sock >= 0)
    {
        close(sock);
        sock = -1;
    }
}
