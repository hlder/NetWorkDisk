attribute vec4 vPosition;
attribute vec4 vCoord;
varying vec2 textureCoordinate;

void main(){
    // 世界坐标系
    gl_Position = vPosition;
    // 布局坐标系
    textureCoordinate = vCoord.xy;
}
