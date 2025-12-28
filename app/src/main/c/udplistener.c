#include <jni.h>

#include <arpa/inet.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include <time.h>
#include <stdio.h>

#define BUF_SIZE 1024
#define LOG_TAG "UDPListener"

static int sock = -1;

static char *udpbroadcast_listener(int port)
{
    struct sockaddr_in addr, client_addr;
    static char buf[BUF_SIZE];
    static char formatted_buf[BUF_SIZE + 256]; // extra space for metadata

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
        socklen_t client_len = sizeof(client_addr);
        int len = recvfrom(sock, buf, BUF_SIZE - 1, 0, (struct sockaddr *)&client_addr, &client_len);
        if (len > 0)
        {
            buf[len] = '\0'; // terminate string
            
            // Get timestamp
            time_t now;
            struct tm *timeinfo;
            char timestamp[64];
            time(&now);
            timeinfo = localtime(&now);
            strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", timeinfo);
            
            // Get source IP
            char *client_ip = inet_ntoa(client_addr.sin_addr);
            
            // Also return formatted string for UI display
            snprintf(formatted_buf, sizeof(formatted_buf), 
                "[%s] Received from %s\n%s", 
                timestamp, client_ip, buf);
            
            close(sock);
            return formatted_buf;
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
