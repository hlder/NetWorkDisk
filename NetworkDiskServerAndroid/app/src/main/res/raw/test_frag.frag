varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

void main(){
//    gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    gl_FragColor = vec4(0,0,255,0);
}